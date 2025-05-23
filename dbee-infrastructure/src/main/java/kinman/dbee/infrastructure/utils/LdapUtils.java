package kinman.dbee.infrastructure.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUtils {

	private static final Logger logger = LoggerFactory.getLogger(LdapUtils.class);
	
	private static String OBJECT_CLASS =  "(|(objectClass=person)(objectClass=inetOrgPerson)(objectClass=organizationalPerson))";
	
	private static String[] RETURN_ATTRS = new String[]{"uid", "cn", "email"};

	public static LdapContext initContext(String url, String dn, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		// LDAP工厂
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// LDAP访问安全级别
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, url);
		// 填DN
		env.put(Context.SECURITY_PRINCIPAL, dn);
		// AD Password
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
		return new InitialLdapContext(env, null);
	}

	public static List<Attributes> searchEntity(LdapContext ldapContext, String baseDn, String searchName) {
		SearchControls contro = new SearchControls();
		contro.setSearchScope(SearchControls.SUBTREE_SCOPE);
		contro.setReturningAttributes(RETURN_ATTRS);
		String filter = OBJECT_CLASS;
		if(!StringUtils.isBlank(searchName)) {
			filter = "(&(|(uid=" + searchName + "*)(cn=" + searchName + "*)(name=" + searchName + "*)(email=" + searchName + "*))" + filter + ")";
		}
		NamingEnumeration<SearchResult> result = null;
		try {
			result = ldapContext.search(baseDn, filter, contro);
		} catch (NamingException e) {
			logger.error("Failed to search ldap po", e);
		}finally {
			closeContext(ldapContext);
		}
		
		if (result == null || !result.hasMoreElements()) {
			logger.warn("No po is found, search name: {}", searchName);
			return null;
		}
		
		List<Attributes> attributesList = new ArrayList<>();
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (!(obj instanceof SearchResult)) {
				continue;
			}
			attributesList.add(((SearchResult)obj).getAttributes());
		}
		return attributesList;
	}
	
	public static Attributes queryEntity(LdapContext ldapCtx, String baseDn, String name) {
		SearchControls contro = new SearchControls();
		contro.setSearchScope(SearchControls.SUBTREE_SCOPE);
		contro.setReturningAttributes(RETURN_ATTRS);
		NamingEnumeration<SearchResult> result = null;
		try {
			result = ldapCtx.search(baseDn, queryFilter(name), contro);
		} catch (NamingException e) {
			logger.error("Failed to search ldap", e);
		}finally {
			closeContext(ldapCtx);
		}
		
		if (result == null || !result.hasMoreElements()) {
			logger.warn("User no found, name: {}", name);
			return null;
		}
		
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (!(obj instanceof SearchResult)) {
				continue;
			}
			return ((SearchResult)obj).getAttributes();
		}
		
		return null;
	}
	
	public static Attributes authByDn(String url, String dn, String password) throws NamingException {
		LdapContext ldapContext = initContext(url, dn, password);
		if(ldapContext == null) {
			return null;
		}
		
		String uid = dn.split(",")[0].split("=")[1];
		String baseDn = dn.substring(dn.indexOf(",") + 1);
		return queryEntity(ldapContext, baseDn, uid);
	}
	
	public static Attributes authByComparePassword(LdapContext ldapCtx, String baseDn, String name, String password) {
		SearchControls contro = new SearchControls();
		contro.setSearchScope(SearchControls.SUBTREE_SCOPE);
		contro.setReturningAttributes(new String[]{"uid", "cn", "email", "password", "userPassword"});
		NamingEnumeration<SearchResult> result = null;
		try {
			result = ldapCtx.search(baseDn, queryFilter(name), contro);
		} catch (NamingException e) {
			logger.error("Failed to search ldap", e);
		}finally {
			closeContext(ldapCtx);
		}
		
		if (result == null || !result.hasMoreElements()) {
			logger.warn("User no found, name: {}", name);
			return null;
		}
		
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (!(obj instanceof SearchResult)) {
				continue;
			}
			
			Attributes attrs = ((SearchResult)obj).getAttributes();
			Attribute attr = attrs.get("password");
			if(attr == null) {
				attr = attrs.get("userPassword");
			}
			if(attr == null) {
				logger.error("User password is empty");
				return null;
			}
			
			Object passwordObj = null;
			try {
				passwordObj = attr.get();
			} catch (NamingException e) {
				logger.error("Failed to auth by password", e);
			}
			
			String passwordLdap = "";
			if(passwordObj instanceof byte[]) {
				passwordLdap = new String((byte[])passwordObj);
			}else {
				passwordLdap = (String)passwordObj;
			}
			
			if(passwordLdap.startsWith("{SSHA}") && EncryptUtils.verifyPassword(passwordLdap, password)) {
				return attrs;
			}
			
			if(password.equals(passwordLdap)) {
				return attrs;
			}
		}
		
		return null;
	}

	private static String queryFilter(String name) {
		return "(&(|(uid=" + name + ")(cn=" + name + ")(name=" + name + ")(email=" + name + "*)) "+ OBJECT_CLASS + ")";
	}
	
	public static void closeContext(LdapContext ldapCtx) {
		if(ldapCtx == null) {
			return;
		}
		try {
			ldapCtx.close();
		} catch (NamingException e) {
			logger.error("Failed to close ldap context", e);
		}
	}
}