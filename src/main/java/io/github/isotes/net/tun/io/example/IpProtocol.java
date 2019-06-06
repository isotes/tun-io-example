/*
 * Copyright (c) 2019 Robert Sauter
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.isotes.net.tun.io.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum IpProtocol {
	HOPOPT(0, "HOPOPT"),
	ICMP(1, "ICMP"),
	IGMP(2, "IGMP"),
	GGP(3, "GGP"),
	IP_IN_IP(4, "IP-in-IP"),
	ST(5, "ST"),
	TCP(6, "TCP"),
	CBT(7, "CBT"),
	EGP(8, "EGP"),
	IGP(9, "IGP"),
	BBN_RCC_MON(10, "BBN-RCC-MON"),
	NVP_II(11, "NVP-II"),
	PUP(12, "PUP"),
	ARGUS(13, "ARGUS"),
	EMCON(14, "EMCON"),
	XNET(15, "XNET"),
	CHAOS(16, "CHAOS"),
	UDP(17, "UDP"),
	MUX(18, "MUX"),
	DCN_MEAS(19, "DCN-MEAS"),
	HMP(20, "HMP"),
	PRM(21, "PRM"),
	XNS_IDP(22, "XNS-IDP"),
	TRUNK_1(23, "TRUNK-1"),
	TRUNK_2(24, "TRUNK-2"),
	LEAF_1(25, "LEAF-1"),
	LEAF_2(26, "LEAF-2"),
	RDP(27, "RDP"),
	IRTP(28, "IRTP"),
	ISO_TP4(29, "ISO-TP4"),
	NETBLT(30, "NETBLT"),
	MFE_NSP(31, "MFE-NSP"),
	MERIT_INP(32, "MERIT-INP"),
	DCCP(33, "DCCP"),
	THREEPC(34, "3PC"),
	IDPR(35, "IDPR"),
	XTP(36, "XTP"),
	DDP(37, "DDP"),
	IDPR_CMTP(38, "IDPR-CMTP"),
	TP_PLUS_PLUS(39, "TP_PLUS_PLUS"),
	IL(40, "IL"),
	IPV6(41, "IPv6"),
	SDRP(42, "SDRP"),
	IPV6_ROUTE(43, "IPv6-Route"),
	IPV6_FRAG(44, "IPv6-Frag"),
	IDRP(45, "IDRP"),
	RSVP(46, "RSVP"),
	GRES(47, "GREs"),
	DSR(48, "DSR"),
	BNA(49, "BNA"),
	ESP(50, "ESP"),
	AH(51, "AH"),
	I_NLSP(52, "I-NLSP"),
	SWIPE(53, "SWIPE"),
	NARP(54, "NARP"),
	MOBILE(55, "MOBILE"),
	TLSP(56, "TLSP"),
	SKIP(57, "SKIP"),
	IPV6_ICMP(58, "IPv6-ICMP"),
	IPV6_NONXT(59, "IPv6-NoNxt"),
	IPV6_OPTS(60, "IPv6-Opts"),
	ANY_HOST_INTERNAL(61, "ANY_HOST_INTERNAL"),
	CFTP(62, "CFTP"),
	ANY_LOCAL_NET(63, "ANY_LOCAL_NET"),
	SAT_EXPAK(64, "SAT-EXPAK"),
	KRYPTOLAN(65, "KRYPTOLAN"),
	RVD(66, "RVD"),
	IPPC(67, "IPPC"),
	ANY_DFS(68, "ANY_DFS"),
	SAT_MON(69, "SAT-MON"),
	VISA(70, "VISA"),
	IPCU(71, "IPCU"),
	CPNX(72, "CPNX"),
	CPHB(73, "CPHB"),
	WSN(74, "WSN"),
	PVP(75, "PVP"),
	BR_SAT_MON(76, "BR-SAT-MON"),
	SUN_ND(77, "SUN-ND"),
	WB_MON(78, "WB-MON"),
	WB_EXPAK(79, "WB-EXPAK"),
	ISO_IP(80, "ISO-IP"),
	VMTP(81, "VMTP"),
	SECURE_VMTP(82, "SECURE-VMTP"),
	VINES(83, "VINES"),
	TTP(84, "TTP"),
	IPTM(84, "IPTM"),
	NSFNET_IGP(85, "NSFNET-IGP"),
	DGP(86, "DGP"),
	TCF(87, "TCF"),
	EIGRP(88, "EIGRP"),
	OSPF(89, "OSPF"),
	SPRITE_RPC(90, "Sprite-RPC"),
	LARP(91, "LARP"),
	MTP(92, "MTP"),
	AX_25(93, "AX.25"),
	OS(94, "OS"),
	MICP(95, "MICP"),
	SCC_SP(96, "SCC-SP"),
	ETHERIP(97, "ETHERIP"),
	ENCAP(98, "ENCAP"),
	ANY_PRIVATE_ENC(99, "ANY_PRIVATE_ENC"),
	GMTP(100, "GMTP"),
	IFMP(101, "IFMP"),
	PNNI(102, "PNNI"),
	PIM(103, "PIM"),
	ARIS(104, "ARIS"),
	SCPS(105, "SCPS"),
	QNX(106, "QNX"),
	ACTIVE_NETWORKS(107, "A/N"),
	IPCOMP(108, "IPComp"),
	SNP(109, "SNP"),
	COMPAQ_PEER(110, "Compaq-Peer"),
	IPX_IN_IP(111, "IPX-in-IP"),
	VRRP(112, "VRRP"),
	PGM(113, "PGM"),
	ANY_ZERO_HOP(114, "ANY_ZERO_HOP"),
	L2TP(115, "L2TP"),
	DDX(116, "DDX"),
	IATP(117, "IATP"),
	STP(118, "STP"),
	SRP(119, "SRP"),
	UTI(120, "UTI"),
	SMP(121, "SMP"),
	SM(122, "SM"),
	PTP(123, "PTP"),
	IS_IS_OVER_IPV4(124, "IS-IS over IPv4"),
	FIRE(125, "FIRE"),
	CRTP(126, "CRTP"),
	CRUDP(127, "CRUDP"),
	SSCOPMCE(128, "SSCOPMCE"),
	IPLT(129, "IPLT"),
	SPS(130, "SPS"),
	PIPE(131, "PIPE"),
	SCTP(132, "SCTP"),
	FC(133, "FC"),
	RSVP_E2E_IGNORE(134, "RSVP-E2E-IGNORE"),
	MOBILITY_HEADER(135, "Mobility Header"),
	UDPLITE(136, "UDPLite"),
	MPLS_IN_IP(137, "MPLS-in-IP"),
	MANET(138, "manet"),
	HIP(139, "HIP"),
	SHIM6(140, "Shim6"),
	WESP(141, "WESP"),
	ROHC(142, "ROHC"),
	RESERVED(255, "Reserved");

	public static final Map<Integer, IpProtocol> map = byValueMap();
	public final int number;
	public final String name;

	IpProtocol(int number, String name) {
		this.number = number;
		this.name = name;
	}

	public static IpProtocol get(int number) {
		return map.get(number);
	}

	public static String safeName(int number) {
		IpProtocol proto = map.get(number);
		return proto != null ? proto.name : "Unknown";
	}

	private static Map<Integer, IpProtocol> byValueMap() {
		Map<Integer, IpProtocol> m = new HashMap<>();
		for (IpProtocol e : IpProtocol.class.getEnumConstants()) {
			m.put(e.number, e);
		}
		return Collections.unmodifiableMap(m);
	}
}
