package share.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

/**
 * @author liyuxiang
 * @date 2021-12-30
 */
public class Demo {

	public static void main(String[] args) {

		try {

			final String resource = "mybatis/MapperConfig.xml";
			final Reader reader = Resources.getResourceAsReader(resource);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

			SqlSession sqlSession = sqlSessionFactory.openSession(false);
			System.out.println("开启sqlSession");

			UserDTO userDTO = new UserDTO();
			userDTO.setName("1111111");

			sqlSession.insert("insert",userDTO);

			/*UserDTOMapper mapper = sqlSession.getMapper(UserDTOMapper.class);
			List<UserDTO> userDTOList = mapper.selectAll();
			System.out.println(JSON.toJSONString(userDTOList));

			List<Object> objects = sqlSession.selectList("share.mybatis.UserDTOMapper.selectAll");
			System.out.println(JSON.toJSONString(objects));*/


			sqlSession.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
