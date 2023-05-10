package user.study.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import user.study.member.domain.user.User;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository {

    private final EntityManager em;

    public Optional<User> findByEmail(String email){
        List<User> users = em.createQuery("select u from User u where email = :email", User.class)
                        .setParameter("email", email)
                        .getResultList();
        if(!users.isEmpty()) return Optional.ofNullable(users.get(0));
        else return Optional.empty();
    }

    public User findById(Long id){
        return em.find(User.class, id);
    }

    public Optional<List<User>> findByName(String name){
        Optional<List<User>> o = Optional.ofNullable(em.createQuery("select u from User u where name = :name", User.class)
                .setParameter("name", name)
                .getResultList());
        log.debug("o: {}",o.toString());
        // Optional가 감싼 객체 가 비어있는지 확인할려면 get해서 객체를 얻고난 뒤 확인하자 (단, 객체가 null이 아닌 것을 확신할 때)
        log.debug("o isEmpty: {}",o.get().isEmpty()); 
        return o;
    }

    public User save(User user){
        log.debug("userName = {} userPwd = {}",user.getName(), user.getPwd());
        em.persist(user);
        return user;
    }

    public Optional<List<User>> findAll(){
        return  Optional.ofNullable(em.createQuery("select u from User u", User.class)
                .getResultList());
    }
}
