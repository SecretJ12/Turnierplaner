package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Tournament;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TournamentRepository implements PanacheRepository<Tournament> {

}
