package com.quote.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.quote.entities.auth.AuthUser;

@Entity
@Table(name="xml_document")
public class XMLDocument {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
	private AuthUser user;
	
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "token")
	private Tokens token;
	
	@Column(name="inf_nfe_id")
	private String infNFeId;
	@Column(name="nnf")
	private int nNF;
	@Column(name="xnome")
	private String xNome;
	@Column(name="demi")
	private String dEmi;
    private String status;
	private String document;
	private Date createdAt;
	private Date updatedAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AuthUser getUser() {
		return user;
	}
	public void setUser(AuthUser user) {
		this.user = user;
	}
	public String getInfNFeId() {
		return infNFeId;
	}
	public void setInfNFeId(String infNFeId) {
		this.infNFeId = infNFeId;
	}
	public int getnNF() {
		return nNF;
	}
	public void setnNF(int nNF) {
		this.nNF = nNF;
	}
	public String getxNome() {
		return xNome;
	}
	public void setxNome(String xNome) {
		this.xNome = xNome;
	}
	public String getdEmi() {
		return dEmi;
	}
	public void setdEmi(String dEmi) {
		this.dEmi = dEmi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Tokens getToken() {
		return token;
	}
	public void setToken(Tokens token) {
		this.token = token;
	}
	
}
