package com.leaf.springData.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.leaf.springData.Person;
import com.leaf.springData.PersonRepository;
import com.leaf.springData.PersonService;

public class SpringDataTest {

	private ApplicationContext ioc = null;
	private PersonRepository repository = null;
	private PersonService personService = null;

	{
		ioc = new ClassPathXmlApplicationContext("classpath*:application*.xml");
		repository = ioc.getBean(PersonRepository.class);
		personService = ioc.getBean(PersonService.class);
	}

	/**
	 * 支持的关键字规范拼接
	 */
	@Test
	public void testKeyWords1() {
		List<Person> list = repository.getByLastNameStartingWithAndIdLessThan("A", 10);
		System.out.println(list);
	}

	/**
	 * 支持的关键字规范拼接
	 */
	@Test
	public void testKeyWords2() {
		List<Person> list = repository.getByLastNameEndingWithAndIdLessThan("A", 10);
		System.out.println(list);
	}

	/**
	 * 支持的关键字规范拼接
	 */
	@Test
	public void testKeyWords3() {
		List<Person> list = repository.getByEmailInOrBirthLessThan(Arrays.asList("com.leaf@aa.com", "com.leaf@bb.com"),
				new Date());
		System.out.println(list);
		list = repository.getByEmailInAndBirthLessThan(Arrays.asList("com.leaf@aa.com", "com.leaf@bb.com"), new Date());
		System.out.println(list);
	}

	/**
	 * 级联查询
	 */
	@Test
	public void testKeyWords4() {
		List<Person> persons = repository.getByAddress_IdGreaterThan(1);
		System.out.println(persons);
	}

	/**
	 * query注解
	 */
	@Test
	public void testQueryAnnotation() {
		Person persons = repository.getMaxIdPerson();
		System.out.println(persons);
	}

	@Test
	public void testEntityManagerFactory() {
	}

	@Test
	public void testFun() {
		Person person = repository.getByLastName("AA");
		System.out.println(person);
	}

	@Test
	public void testPersonRepository() {
		PersonRepository repository = ioc.getBean(PersonRepository.class);
		Person person = repository.getByLastName("AA");
		System.out.println(person);
	}

	@Test
	public void testDataSource() throws Exception {
		DataSource bean = ioc.getBean(DataSource.class);
		System.out.println(bean.getConnection());

	}

	@Test
	public void testQueryAnnotationParams1() {
		List<Person> list = repository.testQueryAnnotationParams1("BA", "com.leaf@aa.com");
		System.out.println(list);
	}

	@Test
	public void testQueryAnnotationParams2() {
		List<Person> list = repository.testQueryAnnotationParams2("com.leaf@aa.com", "BA");

		System.out.println(list);
	}

	@Test
	public void testQueryAnnotationLikeParam() {
		List<Person> list = repository.testQueryAnnotationLikeParam("BA", "leaf");
		System.out.println(list);
	}

	@Test
	public void testQueryAnnotationLikeParam2() {
		List<Person> list = repository.testQueryAnnotationLikeParam2("leaf", "BA");
		System.out.println(list);
	}

	@Test
	public void getTotalCount() {
		long totalCount = repository.getTotalCount();
		System.out.println(totalCount);
	}

	@Test
	public void updatePersonEmail() {
		personService.updatePersonEmail(1, "leaf@gmail.com");
	}

	@Test
	public void saveCurdRepository() {
		List<Person> list = new ArrayList<>();
		for (int i = 'a'; i <= 'z'; i++) {
			Person p = new Person();
			p.setAddressId(i + 1);
			p.setLastName((char) i + "" + (char) i);
			p.setEmail((char) i + "" + (char) i + "@gmail.com");
			p.setBirth(new Date());
			list.add(p);
		}
		personService.savePersons(list);
	}

	@Test
	public void testPagingAndSortingRepository() {
		// Pageable 接口通常使用的其 PageRequest 实现类. 其中封装了需要分页的信息
		// 排序相关的. Sort 封装了排序的信息
		// Order 是具体针对于某一个属性进行升序还是降序.
		Order order1 = new Order(Direction.DESC, "id");
		Order order2 = new Order(Direction.ASC, "email");
		Sort sort = new Sort(order1, order2);

		PageRequest pageable = new PageRequest(3 - 1, 5, sort);
		Page<Person> page = repository.findAll(pageable);

		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}

	@Test
	public void testJpaRepository() {
		Person person = new Person();
		person.setBirth(new Date());
		person.setEmail("leaf59936@gmail.com");
		person.setLastName("Microsoft");
		person.setAddressId(123456);
		person.setAddressId(28);

		Person p = repository.saveAndFlush(person);
		System.out.println(person == p);
	}

	/**
	 * 目标: 实现带查询条件的分页. id > 5 的条件
	 * 
	 * 调用 JpaSpecificationExecutor 的 Page<T> findAll(Specification<T> spec, Pageable
	 * pageable); Specification: 封装了 JPA Criteria 查询的查询条件 Pageable: 封装了请求分页的信息: 例如
	 * pageNo, pageSize, Sort
	 */
	@Test
	public void testJpaSpecificationExecutor() {
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);

		// 通常使用 Specification 的匿名内部类
		Specification<Person> specification = new Specification<Person>() {

			/**
			 * @param *root: 代表查询的实体类. 
			 * @param query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以
			 * 来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象. 
			 * @param *cb: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
			 * @return: *Predicate 类型, 代表一个查询条件. 
			 */
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path path = root.get("id");
				Predicate predicate = cb.gt(path, 5);
				return predicate;
			}
			
		};
		
		Page<Person> page = repository.findAll(specification, pageable);
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}
	
	@Test
	public void testCustomRepositoryMethod() {
     repository.test();		
	}
}
