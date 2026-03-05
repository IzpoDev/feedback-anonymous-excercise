package com.feedback.feedback.modules.feedback.repository;

import com.feedback.feedback.modules.feedback.model.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    @Query("select f from FeedbackEntity f where f.recipient.id=:id")
    List<FeedbackEntity> findAllByRecipientId(@Param("id") Long recipientId);

}
