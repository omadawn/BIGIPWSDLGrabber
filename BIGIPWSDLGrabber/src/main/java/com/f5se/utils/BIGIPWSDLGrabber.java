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
	private static StringBuffer bigipclasssource; 
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
				    bigipclasssource.append("package iControl;\n\n"); 
				    bigipclasssource.append("import java.net.URL;\n");
				    bigipclasssource.append("import java.net.MalformedURLException;\n");
				    bigipclasssource.append("import java.rmi.RemoteException;\n");
				    bigipclasssource.append("import java.security.SecureRandom;\n");
				    bigipclasssource.append("import java.security.cert.X509Certificate;\n");
				    bigipclasssource.append("import javax.net.ssl.SSLContext;\n");
				    bigipclasssource.append("import javax.net.ssl.TrustManager;\n");
				    bigipclasssource.append("import javax.net.ssl.X509TrustManager;\n");
				    bigipclasssource.append("import javax.xml.rpc.ServiceException;\n\n\n\n");

				    bigipclasssource.append("public class BigIP {\n");
				    bigipclasssource.append("   private URL url;\n");
				    bigipclasssource.append("   private String username = \"admin\";\n");
				    bigipclasssource.append("   private String password = \"admin\";\n");
				    bigipclasssource.append("   private boolean ignoreInvalidCert = false;\n");
						
				    bigipclasssource.append("   public BigIP() {\n");
				    bigipclasssource.append("     this.url = getUrl();\n");
				    bigipclasssource.append("   }\n");
						
				    bigipclasssource.append("   public BigIP(String username, String password) {\n");
				    bigipclasssource.append("   		this.username = username;\n");
				    bigipclasssource.append("   		this.password = password;\n");
				    bigipclasssource.append("   		this.url = getUrl();\n");
				    bigipclasssource.append("   }\n");
							
				    bigipclasssource.append("   public URL getUrl() {\n");
				    bigipclasssource.append("		if(url == null) {\n");
				    bigipclasssource.append("			try {\n");
				    bigipclasssource.append("				url = new URL(\"https://\"+getUsername()+\":\"+getPassword()+\"@192.168.100.245/iControl/iControlPortal.cgi\");\n");
				    bigipclasssource.append("			} catch (MalformedURLException e) {\n");
				    bigipclasssource.append("				;\n");
				    bigipclasssource.append("			}\n");
				    bigipclasssource.append("		}\n");
				    bigipclasssource.append("		return url;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setUrl(URL url) {\n");
				    bigipclasssource.append("       this.url = url;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public String getUsername() {\n");
				    bigipclasssource.append("       return username;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setUsername(String username) throws MalformedURLException {\n");
				    bigipclasssource.append("       this.username = username;\n");
				    bigipclasssource.append("       url = getUrl();\n");
				    bigipclasssource.append("       url = new URL(url.getProtocol()+\"://\"+username+\":\"+getPassword()+\"@\"+url.getHost()+\":\"+url.getPort()+\"/iControl/iControlPortal.cgi\");\n");		
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public String getPassword() {\n");
				    bigipclasssource.append("       return password;\n");
				    bigipclasssource.append("   }\n");
				    bigipclasssource.append("   public void setPassword(String password) throws MalformedURLException{\n");
				    bigipclasssource.append("        this.password = password;\n");
				    bigipclasssource.append("        url = getUrl();\n");
				    bigipclasssource.append("        url = new URL(url.getProtocol()+\"://\"+getUsername()+\":\"+password+\"@\"+url.getHost()+\":\"+url.getPort()+\"/iControl/iControlPortal.cgi\");\n");				
				    bigipclasssource.append("   }\n");
		    		bigipclasssource.append("   public void setHost(String host) throws MalformedURLException {\n");
    				bigipclasssource.append("        url = getUrl();\n");
					bigipclasssource.append("        url = new URL(url.getProtocol()+\"://\"+getUsername()+\":\"+getPassword()+\"@\"+host+\":\"+url.getPort()+\"/iControl/iControlPortal.cgi\");\n");
					bigipclasssource.append("   }\n");
							
					bigipclasssource.append("   public boolean isIgnoreInvalidCert() {\n");
					bigipclasssource.append("        return ignoreInvalidCert;\n");
					bigipclasssource.append("   }\n");

					bigipclasssource.append("   public void setIgnoreInvalidCert(boolean ignoreInvalidCert) {\n");
					bigipclasssource.append("       this.ignoreInvalidCert = ignoreInvalidCert;\n");
					bigipclasssource.append("       if(ignoreInvalidCert) {\n");
					bigipclasssource.append("         try {\n");
					bigipclasssource.append("               TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){\n");
					bigipclasssource.append("                 public X509Certificate[] getAcceptedIssuers(){return null;}\n");
					bigipclasssource.append("                 public void checkClientTrusted(X509Certificate[] certs, String authType){}\n");
					bigipclasssource.append("                 public void checkServerTrusted(X509Certificate[] certs, String authType){}\n");
					bigipclasssource.append("               }};\n");
					bigipclasssource.append("               SSLContext ctx = SSLContext.getInstance(\"TLS\");\n");
					bigipclasssource.append("               ctx.init(null, trustAllCerts, new SecureRandom());\n");
					bigipclasssource.append("               SSLContext.setDefault(ctx);\n");
					bigipclasssource.append("        } catch (Exception e) {\n");
					bigipclasssource.append("            e.printStackTrace();\n");
					bigipclasssource.append("            System.exit(1);\n");
					bigipclasssource.append("        }\n");
					bigipclasssource.append("      }\n");
					bigipclasssource.append("   }\n");					
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
		                
				    	filename = grabber.getDestinationDirectory()+"/"+filename+".wsdl";	
				    	int filebytesize = 0;
				    	URL urlLink;
					    try {
					    	urlLink = new URL(bigipurl, strLink);
					    	System.out.print("Copying "+ urlLink +" to "+filename);
					    		
					    	if(generaterBigIPSource) {
								bigipclasssource.append("   public iControl."+axisClassName+"BindingStub get"+axisClassName+"() throws RemoteException, ServiceException {\n");
								bigipclasssource.append("      iControl."+axisClassName+"BindingStub binding = (iControl."+axisClassName+"BindingStub) new iControl."+axisClassName+"Locator().get"+axisClassName+"Port(getUrl());\n");
								bigipclasssource.append("      return binding;\n");
								bigipclasssource.append("   }\n\n");			
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
		        if(generaterBigIPSource) {
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
