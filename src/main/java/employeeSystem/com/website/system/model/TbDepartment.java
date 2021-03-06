package employeeSystem.com.website.system.model;
//default package

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * TbGroup generated by hbm2java
 */
@Entity
@Table(name = "tb_department", catalog = "yesee")
public class TbDepartment implements java.io.Serializable {

	private Integer id;
	private TbEmployees manager;
	private String name;
	private String companyId;
	private Integer parentId;
	private String description;

	public TbDepartment() {
	}

	public TbDepartment(String name) {
		this.name = name;
	}

	public TbDepartment(TbEmployees tbEmployees, String name, String companyId, Integer parentId, String description) {
		this.manager = tbEmployees;
		this.name = name;
		this.companyId = companyId;
		this.parentId = parentId;
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "manager")
	public TbEmployees getManager() {
		return this.manager;
	}

	public void setManager(TbEmployees tbEmployees) {
		this.manager = tbEmployees;
	}

	@Column(name = "name", nullable = false, length = 256)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "company_id", length = 10)
	public String getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	@Column(name = "parent_id")
	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
