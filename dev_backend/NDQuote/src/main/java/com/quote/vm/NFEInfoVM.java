package com.quote.vm;

public class NFEInfoVM {
    private Long id;
	private String infNFeId;
	private int nNF;
	private String xNome;
	private String dEmi;
	private String status;
	private boolean authorised; 
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public boolean isAuthorised() {
		return authorised;
	}
	public void setAuthorised(boolean authorised) {
		this.authorised = authorised;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
