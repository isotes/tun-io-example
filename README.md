# tun-io-example
Example project for the [tun-io](https://github.com/isotes/tun-io) library which provides JNA based access to TUN devices on Linux and macOS.


## Overview
This project serves both as a test and example project for the above-mentioned Java library to access TUN devices. The basic functionality consists of creating and configuring a TUN device followed by a read loop that outputs basic information of each incoming packet and responds to IPv4 and IPv6 ICMP echo requests (pings).


## Build
The project uses the Gradle application plugin and supports, e.g., `./gradlew build` to generate a tar (and zip) archive in build/distributions
and `./gradlew installDist` which creates a start script at `./build/install/tun-io-example/bin/tun-io-example`. The distribution is self-contained and can be run via a JRE on any target system.


## Use
The resulting tool supports a number of command line parameters which can be listed with `--help`. Under macOS, and unless using an existing TUN device with appropriate permissions under Linux, the tool must be run with elevated privileges (`sudo`).

By default, the tool will create a new TUN device (with a name chosen by the OS), assign the IPv6 address `fddf:face:face::5555`, and act as a router for the `fddf:face:face/64` network. Therefore, packets with a destination to that network (except to 5555) are read by the tool. E.g., `ping6 fddf:face:face::2` will result in output similar like this (56 is the number of bytes of the packet):

```console
$ sudo ./build/install/tun-io-example/bin/tun-io-example
Created tun device utun1 and assigning fddf:face:face::5555
Waiting...
  56 IPv6 fddf:face:face::5555 -> fddf:face:face::2  IPv6-ICMP EchoRequest
  56 IPv6 fddf:face:face::5555 -> fddf:face:face::2  IPv6-ICMP EchoRequest
  56 IPv6 fddf:face:face::5555 -> fddf:face:face::2  IPv6-ICMP EchoRequest
```

Since the tool responds to the incoming Echo Request packets, the `ping6` command should indicate a 100% success rate.

To use IPv4 instead, use the `--address` parameter with an IPv4 address.

On Linux, it is also possible to create and configure a TUN device beforehand and, if suitable permissions are configured, run the tool without root. E.g.,

```console
$ sudo ip tuntap add dev jmytun mode tun user $USER group $USER
$ sudo ip -6 addr add fddf:face:face::5555/64 dev jmytun
$ sudo ip link set dev jmytun up
$ ./tun-io-example/bin/tun-io-example --dev jmytun --no-config
```


## Requirements
The tool requires the `ip` command on Linux and the `ifconfig` command on macOS which are usually already available on most systems.


## License
[Apache 2.0](LICENSE)
