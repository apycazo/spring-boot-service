package es.jander.codex.sbs.persistence;

import es.jander.codex.sbs.data.UserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInfoRepository extends CrudRepository<UserInfo, String>
{
    UserInfo findByUsername (String username);

    @Query("SELECT u FROM UserInfo u where u.enabled = :isUserActive")
    List<UserInfo> findAllUsersByStatus(@Param("isUserActive") boolean isUserActive);
}
