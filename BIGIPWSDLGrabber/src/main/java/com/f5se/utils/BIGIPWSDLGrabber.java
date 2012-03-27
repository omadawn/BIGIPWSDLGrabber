package com.f5se.utils;

import java.util.*;
import java.io.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BIGIPWSDLGrabber {

	private String BigIPHostName = "192.168.100.245";
	private int BigIPPort = 443; 
	private String BigIPUsername = "admin";
	private String BigIPPassword = "admin";
	private String destinationDirectory = "./src/main/resources/wsdls";
	private String helperClassDirectory = "./src/main/java";
	private static String WSDLUri = "/iControl/iControlPortal.cgi";
	private static boolean generaterBigIPSource = false;
	private static boolean v11 = false;
	private static StringBuffer bigipclasssource; 
	private static StringBuffer interfacesource;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length > 3) {
			try {
				
				BIGIPWSDLGrabber grabber = new BIGIPWSDLGrabber(args[0],args[1],args[2],args[3]);
				Authenticator.setDefault(grabber.getAuthenticator());

				if(args.length == 5) {
					generaterBigIPSource = true;
				    grabber.setHelperClassDirectory(args[4]);	
				}
				
				if(generaterBigIPSource) {
					
				    bigipclasssource = new StringBuffer();
				    interfacesource = new StringBuffer();
				    bigipclasssource.append("package iControl;\n\n"); 
				    bigipclasssource.append("import java.net.URL;\n");
				    bigipclasssource.append("import java.net.MalformedURLException;\n");
				    bigipclasssource.append("import java.rmi.RemoteException;\n");
				    bigipclasssource.append("import javax.xml.rpc.ServiceException;\n");
				    bigipclasssource.append("import org.apache.commons.logging.Log;\n"); 
				    bigipclasssource.append("import org.apache.commons.logging.LogFactory;\n"); 
				    bigipclasssource.append("import org.apache.axis.client.Stub;\n");		    
				    bigipclasssource.append("import org.apache.axis.client.Service;\n");
				    bigipclasssource.append("import org.apache.axis.configuration.SimpleProvider;\n");
					bigipclasssource.append("import org.apache.axis.configuration.BasicClientConfig;\n");
					bigipclasssource.append("import org.apache.axis.SimpleTargetedChain;\n");
					bigipclasssource.append("import iControl.utils.IControlHTTPSender;\n\n\n\n");
				    
				    bigipclasssource.append("public class BigIP {\n\n");
				    bigipclasssource.append("   private Log log = LogFactory.getLog(getClass());\n");
				    bigipclasssource.append("   java.util.HashMap<String, Service> services = new java.util.HashMap<String, Service>();\n");
				    bigipclasssource.append("   java.util.HashMap<String, Stub> stubs = new java.util.HashMap<String, Stub>();\n");
				    bigipclasssource.append("   java.util.Hashtable<String, String> httpHeaders = new java.util.Hashtable<String,String>();\n");
				    bigipclasssource.append("   java.util.ArrayList<String> interfaces = new java.util.ArrayList<String>();\n");
				    bigipclasssource.append("   private URL url;\n");
				    bigipclasssource.append("   private String username = \"admin\";\n");
				    bigipclasssource.append("   private String password = \"admin\";\n");
				    bigipclasssource.append("   private boolean ignoreInvalidCert = false;\n");
				    bigipclasssource.append("   private SimpleProvider config = new SimpleProvider(new BasicClientConfig());\n");
				    bigipclasssource.append("   private long session_id = -1;\n");
				    bigipclasssource.append("   private String currentFolder = \"Common\";\n");
				    bigipclasssource.append("   private boolean inTransaction = false;\n\n\n\n");
				    
				    bigipclasssource.append("   public BigIP() {\n");
				    bigipclasssource.append("     log.trace(\"entering default constructor BigIP().\");\n");
				    bigipclasssource.append("     this.url = getUrl();\n");	
				    bigipclasssource.append("     initTransport();\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public BigIP(String username, String password) {\n");
				    bigipclasssource.append("     log.trace(\"entering constructor BigIP(username,password).\");\n");
				    bigipclasssource.append("     this.username = username;\n");
				    bigipclasssource.append("     this.password = password;\n");
				    bigipclasssource.append("     this.url = getUrl();\n");
				    bigipclasssource.append("     initTransport();\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public BigIP(String hostname, String username, String password) {\n");
				    bigipclasssource.append("     log.trace(\"entering constructor BigIP(username,password).\");\n");
				    bigipclasssource.append("     this.username = username;\n");
					bigipclasssource.append("     this.password = password;\n");
				    bigipclasssource.append("     initTransport();\n");
					bigipclasssource.append("     try {\n");
					bigipclasssource.append("       setHost(hostname);\n");
					bigipclasssource.append("     } catch (MalformedURLException e) {\n");
					bigipclasssource.append("       log.error(\"Exception\",e);\n");
					bigipclasssource.append("       e.printStackTrace();\n");
					bigipclasssource.append("     }\n");
					bigipclasssource.append("   }\n");				    							
				    bigipclasssource.append("   public BigIP(URL url, String username, String password) {\n");
				    bigipclasssource.append("     log.trace(\"entering constructor BigIP(url,username,password).\");\n");
					bigipclasssource.append("     this.url = url;\n");
					bigipclasssource.append("     setUsername(username);\n");
					bigipclasssource.append("     setPassword(password);\n");
					bigipclasssource.append("     initTransport();\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   private void initTransport() {\n");   
		    		bigipclasssource.append("      SimpleTargetedChain c = new SimpleTargetedChain(new IControlHTTPSender());\n");
					bigipclasssource.append("      config.deployTransport(\"http\", c);\n");
					bigipclasssource.append("      config.deployTransport(\"https\", c);\n");	
					bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public URL getUrl() {\n");
				    bigipclasssource.append("     log.trace(\"entering getUrl()\");\n");
				    bigipclasssource.append("	  if(url == null) {\n");
				    bigipclasssource.append("			try {\n");
				    bigipclasssource.append("				url = new URL(\"https://192.168.100.245:443/iControl/iControlPortal.cgi\");\n");
				    bigipclasssource.append("			} catch (MalformedURLException e) {\n");
					bigipclasssource.append("               log.error(\"Exception\",e);\n");
					bigipclasssource.append("               e.printStackTrace();\n");
					bigipclasssource.append("			}\n");
				    bigipclasssource.append("	  }\n");
				    bigipclasssource.append("	  return url;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setUrl(URL url) {\n");
				    bigipclasssource.append("     log.trace(\"entering setUrl(url).\");\n");
				    bigipclasssource.append("     this.url = url;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public String getUsername() {\n");
				    bigipclasssource.append("     log.trace(\"entering getUsername().\");\n");
				    bigipclasssource.append("     return username;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setUsername(String username) {\n");
				    bigipclasssource.append("     log.trace(\"entering setUsername(username).\");\n");
				    bigipclasssource.append("     this.username = username;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public String getPassword() {\n");
				    bigipclasssource.append("     log.trace(\"entering getPassword().\");\n");
				    bigipclasssource.append("     return password;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setPassword(String password) {\n");
				    bigipclasssource.append("     log.trace(\"entering setPassword(password).\");\n");
				    bigipclasssource.append("     this.password = password;\n");
				    bigipclasssource.append("   }\n");
		    		bigipclasssource.append("   public void setHost(String host) throws MalformedURLException {\n");
		    		bigipclasssource.append("     log.trace(\"entering setHost(host).\");\n");
		    		bigipclasssource.append("     url = getUrl();\n");
					bigipclasssource.append("     url = new URL(url.getProtocol()+\"://\"+host+\":\"+url.getPort()+\"/iControl/iControlPortal.cgi\");\n");
					bigipclasssource.append("   }\n");							
					bigipclasssource.append("   public boolean isIgnoreInvalidCert() {\n");
					bigipclasssource.append("     log.trace(\"entering isIgnoreInvalidCert().\");\n");
					bigipclasssource.append("     return ignoreInvalidCert;\n");
					bigipclasssource.append("   }\n");
					bigipclasssource.append("   public void setIgnoreInvalidCert(boolean ignoreInvalidCert) {\n");
					bigipclasssource.append("     log.trace(\"entering setIgnoreInvalidCert(\"+ignoreInvalidCert+\").\");\n");
					bigipclasssource.append("     this.ignoreInvalidCert = ignoreInvalidCert;\n");
					bigipclasssource.append("     if(ignoreInvalidCert) {\n");
					bigipclasssource.append("            log.info(\"install iControl.utils.XTrustProvider\");\n");
					bigipclasssource.append("            iControl.utils.XTrustProvider.install();\n");
					bigipclasssource.append("     }\n");
					bigipclasssource.append("   }\n");		
					bigipclasssource.append("   public void addHTTPHeader(String headerName, String headerValue) {\n");
					bigipclasssource.append("     log.trace(\"entering addHTTPHeader(\"+headerName+\",\"+headerValue+\").\");\n");
					bigipclasssource.append("     httpHeaders.put(headerName, headerValue);\n");
					bigipclasssource.append("   }\n");
					bigipclasssource.append("   public void removeHTTPHeader(String headerName) {\n");
					bigipclasssource.append("     log.trace(\"entering removeHTTPHeader(\"+headerName+\").\");\n");
					bigipclasssource.append("     httpHeaders.remove(headerName);\n");
					bigipclasssource.append("   }\n");
					bigipclasssource.append("   public java.util.Hashtable<String,String> getHTTPHeaders() {\n");
					bigipclasssource.append("     log.trace(\"entering getHTTPHeaders().\");\n");
					bigipclasssource.append("     return httpHeaders;\n");
					bigipclasssource.append("   }\n\n\n");

		        	interfacesource.append("   /**\n");
		        	interfacesource.append("   * Returns a String array containing all the service interfaces\n");
		        	interfacesource.append("   * available with this library.\n");
		        	interfacesource.append("   *\n");
		        	interfacesource.append("   * @since     1.0\n");
		        	interfacesource.append("   */\n");
					interfacesource.append("   public String[] getInterfaces() {\n");
					
					
				}				
				
				
				File outputdir = new File(grabber.getDestinationDirectory());
				if(! outputdir.exists() ) {
					outputdir.mkdirs();
				}
				
				URL bigipurl = new URL("https://"+grabber.getBigIPHostName()+":"+grabber.getBigIPPort()+WSDLUri);							
				
				TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
				    public X509Certificate[] getAcceptedIssuers(){return null;}
				    public void checkClientTrusted(X509Certificate[] certs, String authType){}
				    public void checkServerTrusted(X509Certificate[] certs, String authType){}
				}};
				
				//Security.insertProviderAt(provider, position)
				//System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.home") + "/.keystore");
				//XTrustProvider.install();
				SSLContext ctx = SSLContext.getInstance("TLS");
		        ctx.init(null, trustAllCerts, new SecureRandom());
		        SSLContext.setDefault(ctx);
		        
		        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());	
		        HttpsURLConnection.setDefaultHostnameVerifier(
		        	new HostnameVerifier() {
		        		@Override
		        		public boolean verify(String arg0, SSLSession arg1) {
		        			return true;
		        		}
		        	});
		        HttpsURLConnection conn = (HttpsURLConnection) bigipurl.openConnection();
		        InputStream urlStream = bigipurl.openStream();
		        byte b[] = new byte[1000];
				int numRead = urlStream.read(b);
				String content = new String(b, 0, numRead);
				while (numRead != -1) {
				    numRead = urlStream.read(b);
				    if (numRead != -1) {
				    	String newContent = new String(b, 0, numRead);
				    	content += newContent;
				    }
				}
				urlStream.close();
				String lowerCaseContent = content.toLowerCase();
				int numFilesRead = 0;
				
				int index = 0;
				while ((index = lowerCaseContent.indexOf("<a", index)) != -1)
				{
				    if ((index = lowerCaseContent.indexOf("href", index)) == -1) 
					break;
				    if ((index = lowerCaseContent.indexOf("=", index)) == -1) 
					break;
				    index++;
				    String remaining = content.substring(index);
				    StringTokenizer st = new StringTokenizer(remaining, "\t\n\r\">#");
				    String strLink = st.nextToken();
				    if(strLink.indexOf("/iControl/iControlPortal.cgi?WSDL=") == 0) {
				        StringTokenizer wst = new StringTokenizer(strLink,"=");
				    	wst.nextToken();
				    	String filename = wst.nextToken();
				    	int period_index = filename.indexOf(".");
				    	String axisClassName = filename.substring(0, period_index) + filename.substring((period_index+1),(period_index+2)).toUpperCase()+filename.substring((period_index+2));
		                String axisNameSpace = filename.substring(0, period_index) + "/" + filename.substring((period_index+1),(period_index+2))+filename.substring((period_index+2));
				    	if(axisClassName.equals("SystemSession")) {
				    		v11 = true;
				    	}
				    	
				    	filename = grabber.getDestinationDirectory()+"/"+filename+".wsdl";	
				    	int filebytesize = 0;
				    	URL urlLink;
					    try {
					    	urlLink = new URL(bigipurl, strLink);
					    	System.out.print("Copying "+ urlLink +" to "+filename);
					    		
					    	if(generaterBigIPSource) {
					        	bigipclasssource.append("   /**\n");
					        	bigipclasssource.append("   * Creates the service interface object for the "+axisClassName+" iControl interface.\n");
					        	bigipclasssource.append("   *\n");
					        	bigipclasssource.append("   * @since     1.0\n");
					        	bigipclasssource.append("   */\n");
								bigipclasssource.append("   public iControl.services."+axisClassName+"BindingStub "+axisClassName+"() throws RemoteException, ServiceException {\n");
								bigipclasssource.append("      log.trace(\"entering "+axisClassName+"\");\n");
								bigipclasssource.append("      if(stubs.containsKey(\""+axisClassName+"\")){\n");
								bigipclasssource.append("           log.info(\"returning "+axisClassName+" binding stub from cache.\");\n");
								bigipclasssource.append("           iControl.services."+axisClassName+"BindingStub binding = (iControl.services."+axisClassName+"BindingStub) stubs.get(\""+axisClassName+"\");\n");
								bigipclasssource.append("           if(session_id >= 0) {\n");
			    	        	bigipclasssource.append("                binding.setHeader(\"urn:iControl:"+axisNameSpace+"\", \"X-iControl-Session\", Long.toString(session_id));\n");
			    	        	bigipclasssource.append("                httpHeaders.put(\"X-iControl-Session\", Long.toString(session_id));\n");
			    	        	bigipclasssource.append("           }\n");
			    	        	bigipclasssource.append("           binding._setProperty(org.apache.axis.transport.http.HTTPConstants.REQUEST_HEADERS, httpHeaders);\n");
								bigipclasssource.append("           return binding;\n");
								bigipclasssource.append("      }else{\n");
								bigipclasssource.append("           log.info(\"creating "+axisClassName+" binding stub for \"+getUrl());\n");
								bigipclasssource.append("           iControl.services."+axisClassName+"Locator locator = new iControl.services."+axisClassName+"Locator(config);\n");
								bigipclasssource.append("           iControl.services."+axisClassName+"BindingStub binding = (iControl.services."+axisClassName+"BindingStub) locator.get"+axisClassName+"Port(getUrl());\n");
								bigipclasssource.append("           locator.setMaintainSession(true);\n");
								bigipclasssource.append("           if(session_id >= 0) {\n");
			    	        	bigipclasssource.append("                binding.setHeader(\"urn:iControl:"+axisNameSpace+"\", \"X-iControl-Session\", Long.toString(session_id));\n");
			    	        	bigipclasssource.append("                httpHeaders.put(\"X-iControl-Session\", Long.toString(session_id));\n");
			    	        	bigipclasssource.append("           }\n");
			    	        	bigipclasssource.append("           binding.setUsername(username);\n");
                                bigipclasssource.append("           binding.setPassword(password);\n");
								bigipclasssource.append("           binding._setProperty(org.apache.axis.transport.http.HTTPConstants.REQUEST_HEADERS, httpHeaders);\n");
								bigipclasssource.append("           log.info(\"adding "+axisClassName+" binding stub to cache.\");\n");
								bigipclasssource.append("           services.put(\""+axisClassName+"\",locator);\n");
								bigipclasssource.append("           stubs.put(\""+axisClassName+"\",binding);\n");
								bigipclasssource.append("           return binding;\n");
								bigipclasssource.append("      }\n\n");
								bigipclasssource.append("   }\n");
								interfacesource.append("          interfaces.add(\""+axisClassName+"\");\n");
							}	
					    	
					    	HttpsURLConnection wsdlconn = (HttpsURLConnection) urlLink.openConnection();
					    	InputStream wsdlstream = wsdlconn.getInputStream();
					    	byte[] buffer = new byte[4096];
					    	int n = -1;
					    	OutputStream out = new FileOutputStream(filename);
					    	while( (n = wsdlstream.read(buffer)) != -1 ) 
					    	{
					    		out.write(buffer,0,n);
					    		filebytesize = filebytesize + n;
					    	}
					    	out.close();
					    	wsdlconn.disconnect();
					    	System.out.println(" - "+filebytesize+" bytes written.");
					    	numFilesRead++;
					    	
					    } catch (MalformedURLException e) {
					    	continue;
					    }
				    }
				}
		        conn.disconnect();
		        
		        interfacesource.append("          return (String[]) interfaces.toArray();\n");
				interfacesource.append("   }\n\n");
		        
		        if(generaterBigIPSource) {		        	
		        	if(v11) {
			        	bigipclasssource.append("    /**\n");
			        	bigipclasssource.append("    * Starts a session with the BigIP. Insert both HTTP headers and SOAP headers.\n");
			        	bigipclasssource.append("    *\n");
			        	bigipclasssource.append("    * @since     1.0\n");
			        	bigipclasssource.append("    */\n");
		        		bigipclasssource.append("   public long getSessionId() throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      log.trace(\"entering getSessionId()\");\n");
		        		bigipclasssource.append("      if(session_id >= 0) {\n");
		        		bigipclasssource.append("      	  return session_id;\n");
		        		bigipclasssource.append("      }else{\n");
		        		bigipclasssource.append("         session_id = SystemSession().get_session_identifier();\n");
		        		bigipclasssource.append("         log.trace(\"adding HTTP header X-iControl-Session:\"+session_id);\n");
		        		bigipclasssource.append("         httpHeaders.put(\"X-iControl-Session\", Long.toString(session_id));\n");
		        		bigipclasssource.append("            return session_id;\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("   }\n");
			        	bigipclasssource.append("    /**\n");
			        	bigipclasssource.append("    * Set the current folder context.\n");
			        	bigipclasssource.append("    *\n");
			        	bigipclasssource.append("    * @since     1.0\n");
			        	bigipclasssource.append("    */\n");
		        		bigipclasssource.append("   public void setCurrentFolder(String folderName) throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      if(session_id < 0) {\n");
		        		bigipclasssource.append("         getSessionId();\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("      currentFolder = folderName;\n");
		        		bigipclasssource.append("      log.trace(\"entering setCurrentFolder(folderName)\");\n");
		        		bigipclasssource.append("      SystemSession().set_active_folder(folderName);\n");
		        		bigipclasssource.append("   }\n");
			        	bigipclasssource.append("    /**\n");
			        	bigipclasssource.append("    * Returns the current folder context.\n");
			        	bigipclasssource.append("    *\n");
			        	bigipclasssource.append("    * @since     1.0\n");
			        	bigipclasssource.append("    */\n");
		        		bigipclasssource.append("   public String getCurrentFolder() throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      log.trace(\"entering getCurrentFolder()\");\n");
		        		bigipclasssource.append("      currentFolder =  SystemSession().get_active_folder();\n");
		        		bigipclasssource.append("      return currentFolder;\n");
		        		bigipclasssource.append("   }\n");
			        	bigipclasssource.append("    /**\n");
			        	bigipclasssource.append("    * Starts a transaction and returns the session id.\n");
			        	bigipclasssource.append("    *\n");
			        	bigipclasssource.append("    * @since     1.0\n");
			        	bigipclasssource.append("    */\n");
		        		bigipclasssource.append("   public long startTransaction() throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      log.trace(\"entering startTransaction()\");\n");
		        		bigipclasssource.append("      if(session_id < 0) {\n");
		        		bigipclasssource.append("         getSessionId();\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("      if(inTransaction) {\n");
		        		bigipclasssource.append("         log.error(\"already in transaction.  Will not start another.\");\n");
		        		bigipclasssource.append("      } else {\n");
		        		bigipclasssource.append("         inTransaction = true;\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("      return SystemSession().start_transaction();\n");
		        		bigipclasssource.append("   }\n");
			        	bigipclasssource.append("    /**\n");
			        	bigipclasssource.append("    * Attempts to commit all commands within a transaction.\n");
			        	bigipclasssource.append("    *\n");
			        	bigipclasssource.append("    * @since     1.0\n");
			        	bigipclasssource.append("    */\n");
		        		bigipclasssource.append("   public void submitTransaction() throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      log.trace(\"submitTransaction()\");\n");
		        		bigipclasssource.append("      if(session_id < 0) {\n");
		        		bigipclasssource.append("         getSessionId();\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("      if(inTransaction) {\n");
		        		bigipclasssource.append("          SystemSession().submit_transaction();\n");
		        		bigipclasssource.append("          inTransaction=false;\n");
		        		bigipclasssource.append("      } else {\n");
		        		bigipclasssource.append("          log.error(\"not in a transaction.  Nothing submit.\");\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("   }\n");
			        	bigipclasssource.append("   /**\n");
			        	bigipclasssource.append("   * Attempts to roll back commands within a transaction.\n");
			        	bigipclasssource.append("   *\n");
			        	bigipclasssource.append("   * @since     1.0\n");
			        	bigipclasssource.append("   */\n");
			        	bigipclasssource.append("   public void rollbackTransaction() throws RemoteException, ServiceException {\n");
		        		bigipclasssource.append("      log.trace(\"entering rollbackTransaction()\");\n");
		        		bigipclasssource.append("      if(session_id < 0) {\n");
		        		bigipclasssource.append("         getSessionId();\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("      if(inTransaction) {\n");
		        		bigipclasssource.append("         SystemSession().rollback_transaction();\n");
		        		bigipclasssource.append("         inTransaction=false;\n");
		        		bigipclasssource.append("      } else {\n");
		        		bigipclasssource.append("          log.error(\"not in a transaction.  Nothing submit.\");\n");
		        		bigipclasssource.append("      }\n");
		        		bigipclasssource.append("   }\n\n");
		        	}
		        	bigipclasssource.append(interfacesource.toString());
		        	
		        	bigipclasssource.append("    /**\n");
		        	bigipclasssource.append("    * Get the version of the BigIP or return error in attempting to.\n");
		        	bigipclasssource.append("    *\n");
		        	bigipclasssource.append("    * @since     1.0\n");
		        	bigipclasssource.append("    */\n");
		        	bigipclasssource.append("    public String ping() {\n");
		        	bigipclasssource.append("   	      log.trace(\"entering ping()\");\n");
		        	bigipclasssource.append("   	      try {\n");
		        	bigipclasssource.append("   	    	  return SystemSystemInfo().get_version();\n");
		        	bigipclasssource.append("   	      } catch (Exception e) {\n");
		        	bigipclasssource.append("   	    	  log.error(\"can not ping because \"+e.getMessage());\n");
		        	bigipclasssource.append("   	    	  return \"ping failed because \"+e.getMessage();\n");
		        	bigipclasssource.append("   	      }\n");
		        	bigipclasssource.append("      }\n");
		        	
		        	
		        	bigipclasssource.append("}\n");
		        	File helperdir = new File(grabber.getHelperClassDirectory()+"/iControl");
		        	System.out.println("Creating helper class to "+helperdir.getPath()+"/BigIP.java");
		        	if(! helperdir.exists()) {
		        		helperdir.mkdirs();
		        	}
		        	File helperclass = new File(helperdir.getPath()+"/BigIP.java");
		        	BufferedWriter out = new BufferedWriter(new FileWriter(helperclass));
		        	out.write(bigipclasssource.toString());
		        	out.close();   		
		        }
		        System.out.println(numFilesRead+" files downloaded from "+bigipurl);
			} catch (MalformedURLException e) {
				System.out.println("\n\nhttps://"+args[0]+WSDLUri+" is not a valid URL.\n\n");
				printUsage();
			} catch (Exception e) {
				System.out.print("\n\nUncaught Exception\n\n");
				e.printStackTrace();
			}
		} else {
			printUsage();
		}
		
	}
	
	public BIGIPWSDLGrabber() {
		
	}
	
	public BIGIPWSDLGrabber(String destinationDirectory, String hostname, String username, String password) {
		this.setDestinationDirectory(destinationDirectory);
		this.setBigIPHostName(hostname);
		this.setBigIPUsername(username);
		this.setBigIPPassword(password);
	}
	
	private static void printUsage() {
		System.out.println("Usage: java -jar BIGIPWSDLGrabber.jar destinationDirectory hostname username password helperclassDestinationDirectory");
	}
	

	public String getBigIPHostName() {
		return BigIPHostName;
	}

	public void setBigIPHostName(String bigIPHostName) {
		if(bigIPHostName.indexOf(":") > 0) {
			StringTokenizer st = new StringTokenizer(bigIPHostName, ":");
			BigIPHostName = st.nextToken();
			BigIPPort = Integer.parseInt(st.nextToken());		
		} else {
			BigIPHostName = bigIPHostName;
		}
	}

	public String getBigIPUsername() {
		return BigIPUsername;
	}

	public void setBigIPUsername(String bigIPUsername) {
		BigIPUsername = bigIPUsername;
	}

	public String getBigIPPassword() {
		return BigIPPassword;
	}

	public void setBigIPPassword(String bigIPPassword) {
		BigIPPassword = bigIPPassword;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public String getHelperClassDirectory() {
		return helperClassDirectory;
	}

	public void setHelperClassDirectory(String helperClassDirectory) {
		this.helperClassDirectory = helperClassDirectory;
	}

	public int getBigIPPort() {
		return BigIPPort;
	}

	public void setBigIPPort(int bigIPPort) {
		BigIPPort = bigIPPort;
	}
	
	public Authenticator getAuthenticator() {
		return new BIGIPAuthenticator(BigIPUsername,BigIPPassword);
	}
	
	public class BIGIPAuthenticator extends Authenticator {
		String bigipusername = "admin";
		String bigippassword = "admin";
		public BIGIPAuthenticator(String username, String password) {
			bigipusername = username;
			bigippassword = password;
		}
	    protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(bigipusername, bigippassword.toCharArray());
	    }
	}
}
