/*
 * Copyright (c) 2019 Robert Sauter
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.isotes.net.tun.io.example;

public class Main {
	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("--readme")) {
			ReadmeExample.main(new String[]{});
			return;
		}

		PingExample.main(args);
	}
}
