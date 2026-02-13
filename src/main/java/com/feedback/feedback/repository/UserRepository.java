package com.feedback.feedback.repository;


import com.feedback.feedback.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u where u.active=true")
    Optional<List<UserEntity>> getAllActiveUsers();
    @Query("select u from UserEntity u where u.username=:username")
    Optional<UserEntity> getUserByUsername(@Param("username") String username);
    @Query("select u from UserEntity u where u.username=:username and u.active=false")
    Optional<UserEntity> getOldUser(@Param("username") String username);
    @Query("select case when count(u) >0 then true else false end from UserEntity u where u.username=:username")
    Boolean existUserByUsername(@Param("username") String username);
    @Query("select case when count(u) >0 then true else false end from UserEntity u where u.id=:id and u.active=true")
    Boolean existUserByIdActive(@Param("id") Long id);
    @Query("select u from UserEntity u where u.username=:username and u.active=:active" )
    Optional<UserEntity> findUsernameAndActive(@Param("username") String username,@Param("active") Boolean active);
    @Query("select u from UserEntity u where u.username=:username")
    Optional<UserEntity> findByUsername(@Param("username") String username);
}
