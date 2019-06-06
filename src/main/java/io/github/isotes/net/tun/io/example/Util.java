/*
 * Copyright (c) 2019 Robert Sauter
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.isotes.net.tun.io.example;

import com.google.common.collect.ObjectArrays;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;

public class Util {
	public static void exec(String... command) throws IOException {
		int exit;
		try {
			exit = new ProcessExecutor().command(command).redirectOutput(System.out).execute().getExitValue();
		} catch (Exception e) {
			throw new IOException("Executing '" + String.join(" ", command) + "' failed");
		}
		if (exit != 0) {
			throw new IOException("Executing '" + String.join(" ", command) + "' returned error " + exit);
		}
	}

	public static void sudo(String... command) throws IOException {
		exec(ObjectArrays.concat("sudo", command));
	}
}
