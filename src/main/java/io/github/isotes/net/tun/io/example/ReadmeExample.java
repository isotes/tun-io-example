/*
 * Copyright (c) 2019 Robert Sauter
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.isotes.net.tun.io.example;

import io.github.isotes.net.tun.io.Packet;
import io.github.isotes.net.tun.io.TunDevice;

import java.io.IOException;

public class ReadmeExample {
	public static void main(String[] args) {
		try (TunDevice tun = TunDevice.open()) {
			System.out.println("Created tun device " + tun.getName());
			// device configuration (address, ...) required
			while (true) {
				Packet packet = tun.read();
				System.out.printf("IPv%d packet with %d bytes\n", packet.ipVersion(), packet.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
