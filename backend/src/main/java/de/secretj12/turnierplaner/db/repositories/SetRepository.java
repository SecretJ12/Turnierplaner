package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Set;
import de.secretj12.turnierplaner.model.user.jUserSet;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SetRepository implements PanacheRepository<Set> {
  public Set findById(Set.SetKey key) {
    return find("key", key).firstResult();
  }

  public Set findById(Match match, byte index) {
    Set.SetKey setKey = new Set.SetKey();
    setKey.setMatch(match);
    setKey.setIndex(index);
    return findById(setKey);
  }

  @Transactional
  public void updateSets(Match match, List<jUserSet> sets) {}
}
