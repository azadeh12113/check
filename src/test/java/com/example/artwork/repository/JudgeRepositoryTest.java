package com.example.artwork.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.artwork.model.Judge;

@RunWith(SpringRunner.class)
@DataJpaTest

public class JudgeRepositoryTest {

  @Autowired
  private JudgeRepository repository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testFindFirstByOrderByIdAsc() {

    Judge j1 = new Judge();
    j1.setName("J1");
    j1 = entityManager.persistFlushFind(j1);

    Judge j2 = new Judge();
    j2.setName("J2");
    j2 = entityManager.persistFlushFind(j2);

    Judge found = repository.findFirstByOrderByIdAsc();

    Long minId = Math.min(j1.getId(), j2.getId());
    assertThat(found.getId()).isEqualTo(minId);
  }
}
	