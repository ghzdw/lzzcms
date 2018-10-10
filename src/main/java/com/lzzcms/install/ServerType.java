package com.lzzcms.install;

/**
 *  服务器类型探测
 * c.getResource(path):得到给定路径的绝对路径
 * 1./开头，/代表存放.class文件的最上层包的文件夹比如WEB-INF/classes或build等;
 * 2.不以/开头指从相对于当前类的.class文件的位置来找;
 * 3.为""当前类.class所在目录
 * @Date 2011/04/13
 * **/
public class ServerType {
	private static final String GERONIMO_CLASS = "/org/apache/geronimo/system/main/Daemon.class";
	private static final String JONAS_CLASS = "/org/objectweb/jonas/server/Server.class";
	private static final String OC4J_CLASS = "/oracle/jsp/oc4jutil/Oc4jUtil.class";
	private static final String ORION_CLASS = "/com/evermind/server/ApplicationServer.class";
	private static final String PRAMATI_CLASS = "/com/pramati/Server.class";
	private static final String RESIN_CLASS = "/com/caucho/server/resin/Resin.class";
	private static final String REXIP_CLASS = "/com/tcc/Main.class";
	private static final String SUN7_CLASS = "/com/iplanet/ias/tools/cli/IasAdminMain.class";
	private static final String SUN8_CLASS = "/com/sun/enterprise/cli/framework/CLIMain.class";
	private static final String JBOSS_CLASS = "/org/jboss/Main.class";
	private static final String JETTY_CLASS = "/org/mortbay/jetty/Server.class";
	private static final String TOMCAT_CLASS = "/org/apache/catalina/startup/Bootstrap.class";
	private static final String WEBLOGIC_CLASS = "/weblogic/Server.class";
	private static final String WEBSPHERE_CLASS = "/com/ibm/websphere/product/VersionInfo.class";
	
	private static ServerType serverType = new ServerType();
	private String _serverId;
	private Boolean _geronimo;
	private Boolean _jonas;
	private Boolean _oc4j;
	private Boolean _orion;
	private Boolean _pramati;
	private Boolean _resin;
	private Boolean _rexIP;
	private Boolean _sun7;
	private Boolean _sun8;
	private Boolean _jBoss;
	private Boolean _jetty;
	private Boolean _tomcat;
	private Boolean _webLogic;
	private Boolean _webSphere;
	
	public static String getServerId() {
		if (serverType._serverId == null) {
			if (ServerType.isGeronimo()) {
				serverType._serverId = "geronimo";
			} else if (ServerType.isJBoss()) {
				serverType._serverId = "jboss";
			} else if (ServerType.isJOnAS()) {
				serverType._serverId = "jonas";
			} else if (ServerType.isOC4J()) {
				serverType._serverId = "oc4j";
			} else if (ServerType.isOrion()) {
				serverType._serverId = "orion";
			} else if (ServerType.isResin()) {
				serverType._serverId = "resin";
			} else if (ServerType.isWebLogic()) {
				serverType._serverId = "weblogic";
			} else if (ServerType.isWebSphere()) {
				serverType._serverId = "websphere";
			}
			if (ServerType.isJetty()) {
				if (serverType._serverId == null) {
					serverType._serverId = "jetty";
				} else {
					serverType._serverId += "-jetty";
				}
			} else if (ServerType.isTomcat()) {
				if (serverType._serverId == null) {
					serverType._serverId = "tomcat";
				} else {
					serverType._serverId += "-tomcat";
				}
			}
			if (serverType._serverId == null) {
				throw new RuntimeException("Server is not supported");
			}
		}
		return serverType._serverId;
	}

	private static boolean isGeronimo() {
		if (serverType._geronimo == null) {
			Class c = serverType.getClass();
			if (c.getResource(GERONIMO_CLASS) != null) {
				serverType._geronimo = Boolean.TRUE;
			} else {
				serverType._geronimo = Boolean.FALSE;
			}
		}
		return serverType._geronimo.booleanValue();
	}

	private static boolean isJBoss() {
		if (serverType._jBoss == null) {
			Class c = serverType.getClass();
			if (c.getResource(JBOSS_CLASS) != null) {
				serverType._jBoss = Boolean.TRUE;
			} else {
				serverType._jBoss = Boolean.FALSE;
			}
		}
		return serverType._jBoss.booleanValue();
	}

	private static boolean isJetty() {
		if (serverType._jetty == null) {
			Class c = serverType.getClass();
			if (c.getResource(JETTY_CLASS) != null) {
				serverType._jetty = Boolean.TRUE;
			} else {
				serverType._jetty = Boolean.FALSE;
			}
		}
		return serverType._jetty.booleanValue();
	}

	private static boolean isJOnAS() {
		if (serverType._jonas == null) {
			Class c = serverType.getClass();
			if (c.getResource(JONAS_CLASS) != null) {
				serverType._jonas = Boolean.TRUE;
			} else {
				serverType._jonas = Boolean.FALSE;
			}
		}
		return serverType._jonas.booleanValue();
	}

	private static boolean isOC4J() {
		if (serverType._oc4j == null) {
			Class c = serverType.getClass();
			if (c.getResource(OC4J_CLASS) != null) {
				serverType._oc4j = Boolean.TRUE;
			} else {
				serverType._oc4j = Boolean.FALSE;
			}
		}
		return serverType._oc4j.booleanValue();
	}

	private static boolean isOrion() {
		if (serverType._orion == null) {
			Class c = serverType.getClass();
			if (c.getResource(ORION_CLASS) != null) {
				serverType._orion = Boolean.TRUE;
			} else {
				serverType._orion = Boolean.FALSE;
			}
		}
		return serverType._orion.booleanValue();
	}

	private static boolean isPramati() {
		if (serverType._pramati == null) {
			Class c = serverType.getClass();
			if (c.getResource(PRAMATI_CLASS) != null) {
				serverType._pramati = Boolean.TRUE;
			} else {
				serverType._pramati = Boolean.FALSE;
			}
		}
		return serverType._pramati.booleanValue();
	}

	private static boolean isResin() {
		if (serverType._resin == null) {
			Class c = serverType.getClass();
			if (c.getResource(RESIN_CLASS) != null) {
				serverType._resin = Boolean.TRUE;
			} else {
				serverType._resin = Boolean.FALSE;
			}
		}
		return serverType._resin.booleanValue();
	}

	private static boolean isRexIP() {
		if (serverType._rexIP == null) {
			Class c = serverType.getClass();
			if (c.getResource(REXIP_CLASS) != null) {
				serverType._rexIP = Boolean.TRUE;
			} else {
				serverType._rexIP = Boolean.FALSE;
			}
		}
		return serverType._rexIP.booleanValue();
	}

	private static boolean isSun() {
		if (isSun7() || isSun8()) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isSun7() {
		if (serverType._sun7 == null) {
			Class c = serverType.getClass();
			if (c.getResource(SUN7_CLASS) != null) {
				serverType._sun7 = Boolean.TRUE;
			} else {
				serverType._sun7 = Boolean.FALSE;
			}
		}
		return serverType._sun7.booleanValue();
	}

	private static boolean isSun8() {
		if (serverType._sun8 == null) {
			Class c = serverType.getClass();
			if (c.getResource(SUN8_CLASS) != null) {
				serverType._sun8 = Boolean.TRUE;
			} else {
				serverType._sun8 = Boolean.FALSE;
			}
		}
		return serverType._sun8.booleanValue();
	}

	private static boolean isTomcat() {
		if (serverType._tomcat == null) {
			Class c = serverType.getClass();
			if (c.getResource(TOMCAT_CLASS) != null) {
				serverType._tomcat = Boolean.TRUE;
			} else {
				serverType._tomcat = Boolean.FALSE;
			}
		}
		return serverType._tomcat.booleanValue();
	}

	private static boolean isWebLogic() {
		if (serverType._webLogic == null) {
			Class c = serverType.getClass();
			if (c.getResource(WEBLOGIC_CLASS) != null) {
				serverType._webLogic = Boolean.TRUE;
			} else {
				serverType._webLogic = Boolean.FALSE;
			}
		}
		return serverType._webLogic.booleanValue();
	}

	private static boolean isWebSphere() {
		if (serverType._webSphere == null) {
			Class c = serverType.getClass();
			if (c.getResource(WEBSPHERE_CLASS) != null) {
				serverType._webSphere = Boolean.TRUE;
			} else {
				serverType._webSphere = Boolean.FALSE;
			}
		}
		return serverType._webSphere.booleanValue();
	}

}