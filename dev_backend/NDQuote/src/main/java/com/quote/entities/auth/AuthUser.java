package com.quote.entities.auth;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name="authusers")
public class AuthUser implements UserDetails {
	
	private static final long serialVersionUID = 2097496846486828450L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String email;
	private String gender;
	private String nationality;
	private String city;
	private String job;
	private String about;
	private String maritalStatus;
	private int age;
	
	private String profile_pic;
	private String provider;
	private String providerId; 
	
	private String accessToken;
	private Date createdDate;
	
	private boolean enabled ;
		
	private List<Role> roles = new ArrayList<Role>();
	
	@Transient
	private List<PermissionMatrix> permissions = new ArrayList<PermissionMatrix>();
	
	

	@Transient
	public List<PermissionMatrix> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionMatrix> permissions) {
		this.permissions = permissions;
	}


	  public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Transient
    @Transactional
    public Collection<GrantedAuthority> getAuthorities() {
    	List<GrantedAuthority> roles =  new ArrayList<GrantedAuthority>();
		roles.addAll(getRoles());
		return roles;
    }

    @Transient
    public boolean isAccountNonExpired() {
    	return true;
    }

    @Transient
    public boolean isAccountNonLocked() {
    	return true;
    }

    @Transient
    public boolean isCredentialsNonExpired() {
    	return true;
    }


    /* non UserDetails methods */
    @Id
    @Column(name="auth_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() { return id; }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() { return username; }
    
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() { return password; }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Column(name="email_id")
    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }
    
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="userrole",
	       joinColumns=@JoinColumn(name="user_id"),
	       inverseJoinColumns=@JoinColumn(name="role_id"))
    public List<Role> getRoles() { return roles; }

    public void setRoles(List<Role> roles) {
    	this.roles = roles;
    }
    
    
    // ============================= Commented below as it is WIP for ACL =========================//
    
    /*
    public List<Group> groups = new ArrayList<Group>();
    
    public List<PermissionMatrix> permissionMatrix = new ArrayList<PermissionMatrix>();
	
    @Transient
    public Map<String, Integer> privResourceMap;
    
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    public List<Group> getGroups() { return groups; }
    
    public void setGroups(List<Group> groups) {
    	this.groups = groups;
    }
    
    @OneToMany
    @JoinTable(
	       joinColumns=@JoinColumn(name="user_id"),
	       inverseJoinColumns=@JoinColumn(name="permisionmatrix_id"))
    public List<PermissionMatrix> getPermissionMatrix() { return permissionMatrix; }
    
    public void setPermissionMatrix(List<PermissionMatrix> permissionMatrix) {
    	this.permissionMatrix = permissionMatrix;
    }
   
    */
    //@Transient
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
    
    public AuthUser(){}
    
    public AuthUser(String username, String password, List<Role> role, boolean enabled) {
    	this.username = username;
		this.password = password;
		this.roles = role;
		this.enabled = enabled;
		
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	

}
