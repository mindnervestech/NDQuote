package com.quote.auth;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quote.entities.auth.AuthUser;
import com.quote.entities.auth.PermissionMatrix;
import com.quote.entities.auth.Role;



@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public final UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	AuthUser user = null;
    	Session session = null;
    	try {
    	  try {
    		  
    		  session = sessionFactory.getCurrentSession();
    		  user = (AuthUser) session.createQuery("from AuthUser where username = :username").setString("username", username).uniqueResult();
    		  List<PermissionMatrix> permissions = new ArrayList<>();
    		  for(Role role : user.getRoles()) {
    			  permissions.addAll(role.getPermissionMatrix());
    		  } 
    		  user.setPermissions(permissions);
    		 
    	  } catch(org.hibernate.HibernateException ex) {
    		  try {
    			  session = sessionFactory.openSession();
    			  user = (AuthUser) session.createQuery("from AuthUser where username = :username").setString("username", username).uniqueResult();
    		  } finally {
    	    		session.close();
    	    	}
    	  }
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        
        return user;
    }
    
    
    public AuthUser getUserByUserName(String username){
    	Session session = sessionFactory.getCurrentSession();
    	  AuthUser user = (AuthUser) session.createQuery("from AuthUser where username = :username").setString("username", username).uniqueResult();
    	   if(user!=null){
    		    return user;    
             }else{
               return null; 
             }
    }
    
    public AuthUser getUserByUserNameAndProvider(String username,String provider){
    	Session session = sessionFactory.getCurrentSession();
    	  AuthUser user = (AuthUser) session.createQuery("from AuthUser where username = :username AND provider=:provider").setString("username", username).setString("provider", provider).uniqueResult();
    	   if(user!=null){
    		    return user;    
             }else{
               return null; 
             }
    }
    
    public void createUser(AuthUser authUser)
	{
		Session session = sessionFactory.getCurrentSession(); 
	    session.save(authUser);
	}
	

}