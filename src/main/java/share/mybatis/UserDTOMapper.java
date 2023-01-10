package share.mybatis;

import java.util.List;

/**
 * @author liyuxiang
 * @date 2021-12-30
 */
public interface UserDTOMapper {

	List<UserDTO> selectAll();

	int insert(UserDTO userDTO);

}
