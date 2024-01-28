package cn.wolfcode.repository;

import cn.wolfcode.domain.TravelComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelCommentRepository extends MongoRepository<TravelComment, String> {
}
