package pl.piomin.services.department.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.piomin.services.department.model.Department;
import pl.piomin.services.department.model.Employee;
import pl.piomin.services.department.repository.DepartmentRepository;

@RestController
public class DepartmentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);
	
	@Autowired
	DepartmentRepository repository;

	@Autowired
	RestTemplate client;
	
	
	@Value("${URL_EMPLOYEE_SERVICE}")
	private String employeeUrl;
	
	@PostMapping("/")
	public Department add(@RequestBody Department department) {
		LOGGER.info("Department add: {}", department);
		return repository.add(department);
	}
	
	@GetMapping("/{id}")
	public Department findById(@PathVariable("id") Long id) {
		LOGGER.info("Department find: id={}", id);
		return repository.findById(id);
	}
	
	@GetMapping("/")
	public List<Department> findAll() {
		LOGGER.info("Department find");
		return repository.findAll();
	}
	
	@GetMapping("/organization/{organizationId}")
	public List<Department> findByOrganization(@PathVariable("organizationId") Long organizationId) {
		LOGGER.info("Department find: organizationId={}", organizationId);
		return repository.findByOrganization(organizationId);
	}
	
	@GetMapping("/organization/{organizationId}/with-employees")
	public List<Department> findByOrganizationWithEmployees(@PathVariable("organizationId") Long organizationId) {
		LOGGER.info("Department find: organizationId={}", organizationId);
		List<Department> departments = repository.findByOrganization(organizationId);

		departments.forEach(d -> d.setEmployees(getEmployeesByDeparment((d.getId()))));
		return departments;
	}

	private List<Employee> getEmployeesByDeparment(long id){
		RestTemplate restTemplate = new RestTemplate();
		
		/*ResponseEntity<List<Employee>> response = restTemplate.exchange(
			"http://localhost:8085/employee-service/department/"+id,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<Employee>>(){});
		*/
		LOGGER.info("url employee:"+employeeUrl);
		ResponseEntity<List<Employee>> response = restTemplate.exchange(
				employeeUrl+id,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Employee>>(){});	
		List<Employee> employees = response.getBody();
		return employees;
	}
	
}
