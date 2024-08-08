package kr.giljabi.gateway.repository;

import kr.giljabi.gateway.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserInfoRepository extends CrudRepository<UserInfo, String> {
    UserInfo findByUserId(String userId);
}
