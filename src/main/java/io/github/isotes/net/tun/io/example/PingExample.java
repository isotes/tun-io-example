/*
 * Copyright (c) 2019 Robert Sauter
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.isotes.net.tun.io.example;

import com.google.common.collect.ObjectArrays;
import com.google.common.net.InetAddresses;
import io.github.isotes.net.tun.io.Packet;
import io.github.isotes.net.tun.io.TunDevice;
import org.zeroturnaround.exec.ProcessExecutor;
import picocli.CommandLine;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.github.isotes.net.tun.io.example.Util.sudo;

@CommandLine.Command(description = "Create tun device, start ping6 and respond to ping packets.",
		name = "tun-io-example", mixinStandardHelpOptions = true, version = "1.0")
public class PingExample implements Callable<Void> {
	private static final int IPV4_OFFSET_PROTOCOL = 9;
	private static final int IPV4_OFFSET_SRC_ADDR = 12;
	private static final int IPV4_OFFSET_DST_ADDR = 16;
	private static final int IPV4_OFFSET_ICMP_TYPE = 20;
	private static final int IPV4_OFFSET_ICMP_CHECKSUM = IPV4_OFFSET_ICMP_TYPE + 2;
	private static final int ICMP_TYPE_ECHO_REQUEST = 8;
	private static final int ICMP_TYPE_ECHO_REPLY = 0;

	private static final int IPV6_OFFSET_NEXT_HEADER = 6;
	private static final int IPV6_OFFSET_SRC_ADDR = 8;
	private static final int IPV6_OFFSET_DST_ADDR = 8 + 16;
	private static final int IPV6_OFFSET_ICMP_TYPE = 8 + 16 + 16;
	private static final int IPV6_OFFSET_ICMP_CHECKSUM = IPV6_OFFSET_ICMP_TYPE + 2;
	private static final int ICMPV6_TYPE_ECHO_REQUEST = 128;
	private static final int ICMPV6_TYPE_ECHO_REPLY = 129;
	private static final int ICMPV6_TYPE_ROUTER_SOLICITATION = 133;

	@CommandLine.Option(names = {"-a", "--address"}, description = "IP address for the gateway (default: ${DEFAULT-VALUE})")
	private String address = "fddf:face:face::5555";

	@CommandLine.Option(names = "--dev", description = "Name for the TUN device")
	private String dev = "";

	@CommandLine.Option(names = "--pings", description = "Start ping -c <pings>")
	private int pings = 0;

	@CommandLine.Option(names = "--exit-after-ping", description = "Exit after the ping command terminates")
	private boolean exitAfterPing = false;

	@CommandLine.Option(names = "--req-for-req", description = "Answer Echo Request with own request")
	private boolean reqForReq = false;

	@CommandLine.Option(names = "--no-config", description = "Do not configure the interface")
	private boolean noConfig = false;

	private boolean shutDown = false;

	public static void main(String[] args) {
		CommandLine.call(new PingExample(), args);
		System.exit(0);
	}

	private String changeAddressBit(InetAddress address, int bit) throws UnknownHostException {
		byte[] bytes = address.getAddress();
		bytes[bytes.length - 1] ^= 1 << bit;
		return InetAddresses.toAddrString(InetAddress.getByAddress(bytes));
	}

	private void ifconfig(String... command) throws IOException {
		sudo(ObjectArrays.concat("/sbin/ifconfig", command));
	}

	private void route(String... command) throws IOException {
		sudo(ObjectArrays.concat("/sbin/route", command));
	}

	private void ip(String... command) throws IOException {
		sudo(ObjectArrays.concat("/sbin/ip", command));
	}

	private void config(TunDevice tun, InetAddress addr) throws IOException {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			if (addr instanceof Inet6Address) {
				ifconfig(tun.getName(), "inet6", "add", address + "/64");
				ifconfig(tun.getName(), "up");
			} else {
				// for IPv4 macOS seems to require a device address and manual setup of the route
				String d = changeAddressBit(addr, 2);
				ifconfig(tun.getName(), "add", address, d);
				ifconfig(tun.getName(), "up");
				route("add", "-net", address + "/24", "-iface", tun.getName());
			}
		} else {
			if (addr instanceof Inet6Address) {
				ip("-6", "addr", "add", address + "/64", "dev", tun.getName());
				ip("link", "set", "dev", tun.getName(), "up");
			} else {
				ip("addr", "add", address + "/24", "dev", tun.getName());
				ip("link", "set", "dev", tun.getName(), "up");
			}
		}

	}

	@Override
	public Void call() {
		try (TunDevice tun = TunDevice.open(dev)) {
			InetAddress addr = InetAddress.getByName(address);
			System.out.println("Created tun device " + tun.getName() + " and assigning " + InetAddresses.toAddrString(addr));
			if (!noConfig) {
				config(tun, addr);
			}
			if (pings > 0) {
				String pingCmd = addr instanceof Inet6Address ? "ping6" : "ping";
				String d = changeAddressBit(addr, 1);
				System.out.println(String.join(" ", pingCmd, "-c", Integer.toString(pings), "-i", "1", d));
				Process pingProcess = new ProcessExecutor()
						.command(pingCmd, "-c", Integer.toString(pings), "-i", "1", d)
						.redirectOutput(System.err)
						.redirectError(System.err)
						.destroyOnExit()
						.start().getProcess();
				if (exitAfterPing) {
					new Thread(() -> {
						try {
							pingProcess.waitFor(pings + 2, TimeUnit.SECONDS);
							Thread.sleep(1000);
							System.out.println("Exiting after ping finished");
							shutDown = true;
							tun.close();
							Thread.sleep(1000);
							System.exit(0);
						} catch (InterruptedException | IOException e) {
							e.printStackTrace();
						}
					}).start();
				}
			}
			System.out.println("Waiting...");
			while (!shutDown) {
				Packet packet = tun.read();
				if (packet.isIpv4()) {
					handleIPv4(tun, packet);
				} else {
					handleIPv6(tun, packet);
				}
			}
		} catch (IOException e) {
			if (!shutDown) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void handleIPv4(TunDevice tun, Packet packet) throws IOException {
		InetAddress src = InetAddress.getByAddress(packet.bytes(IPV4_OFFSET_SRC_ADDR, 4));
		InetAddress dst = InetAddress.getByAddress(packet.bytes(IPV4_OFFSET_DST_ADDR, 4));
		int protocol = packet.uint8(IPV4_OFFSET_PROTOCOL);
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%4d IPv4 %s -> %s  ", packet.size(), src.getHostAddress(), dst.getHostAddress()))
				.append(IpProtocol.safeName(protocol));
		if (protocol == IpProtocol.ICMP.number) {
			int icmpType = packet.uint8(IPV4_OFFSET_ICMP_TYPE);
			switch (icmpType) {
				case ICMP_TYPE_ECHO_REQUEST:
					sb.append(" EchoRequest");
					packet.bytes(IPV4_OFFSET_DST_ADDR, src.getAddress());
					packet.bytes(IPV4_OFFSET_SRC_ADDR, dst.getAddress());
					if (reqForReq) {
						tun.write(packet);
					}
					packet.uint16(IPV4_OFFSET_ICMP_CHECKSUM, packet.uint16(IPV4_OFFSET_ICMP_CHECKSUM) + 0x0800);
					packet.uint8(IPV4_OFFSET_ICMP_TYPE, ICMP_TYPE_ECHO_REPLY);
					tun.write(packet);
					break;
				case ICMP_TYPE_ECHO_REPLY:
					sb.append(" EchoReply");
					break;
				default:
					sb.append("type=").append(icmpType);
			}
		}
		System.out.println(sb);
	}

	private void handleIPv6(TunDevice tun, Packet packet) throws IOException {
		InetAddress src = InetAddress.getByAddress(packet.bytes(IPV6_OFFSET_SRC_ADDR, 16));
		InetAddress dst = InetAddress.getByAddress(packet.bytes(IPV6_OFFSET_DST_ADDR, 16));
		int nextHeader = packet.uint8(IPV6_OFFSET_NEXT_HEADER);
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%4d IPv6 %s -> %s  ", packet.size(), InetAddresses.toAddrString(src), InetAddresses.toAddrString(dst)))
				.append(IpProtocol.safeName(nextHeader));
		if (nextHeader == IpProtocol.IPV6_ICMP.number) {
			int icmpType = packet.uint8(IPV6_OFFSET_ICMP_TYPE);
			switch (icmpType) {
				case ICMPV6_TYPE_ECHO_REQUEST:
					sb.append(" EchoRequest");
					packet.bytes(IPV6_OFFSET_DST_ADDR, src.getAddress());
					packet.bytes(IPV6_OFFSET_SRC_ADDR, dst.getAddress());
					if (reqForReq) {
						tun.write(packet);
					}
					packet.uint16(IPV6_OFFSET_ICMP_CHECKSUM, packet.uint16(IPV6_OFFSET_ICMP_CHECKSUM) - 0x100);
					packet.uint8(IPV6_OFFSET_ICMP_TYPE, ICMPV6_TYPE_ECHO_REPLY);
					tun.write(packet);
					break;
				case ICMPV6_TYPE_ECHO_REPLY:
					sb.append(" EchoReply");
					break;
				case ICMPV6_TYPE_ROUTER_SOLICITATION:
					sb.append(" RouterSolicitation");
					break;
				default:
					sb.append("type=").append(icmpType);
			}
		}
		System.out.println(sb);
	}
}
