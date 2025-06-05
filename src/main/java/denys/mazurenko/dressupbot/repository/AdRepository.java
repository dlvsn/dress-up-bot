package denys.mazurenko.dressupbot.repository;

import denys.mazurenko.dressupbot.model.Advertisement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Advertisement, Long> {
    @EntityGraph(attributePaths = "images")
    List<Advertisement> findAll();

    @EntityGraph(attributePaths = "images")
    Optional<Advertisement> findById(Long id);
}
