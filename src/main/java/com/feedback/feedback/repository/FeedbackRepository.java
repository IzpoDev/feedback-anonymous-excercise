package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    @Query("select f from FeedbackEntity f where f.recipient.id=:id")
    Optional<List<FeedbackEntity>> findAllByRecipientId(@Param("id") Long recipientId);

}
