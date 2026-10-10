package code.filipesz.springtransactionengine.repositories;

import code.filipesz.springtransactionengine.entities.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {
}