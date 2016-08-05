package io.pivotal.literx;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

/**
 * Learn how to transform values.
 *
 * @author Sebastien Deleuze
 */
public class Part03Transform {

	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void transformMono() {
		final Mono<User> mono = repository.findFirst();
		TestSubscriber
				.subscribe(capitalizeOne(mono))
				.await()
				.assertValues(new User("SWHITE", "SKYLER", "WHITE"))
				.assertComplete();
	}

	// TODO Capitalize the user username, firstname and lastname
	Mono<User> capitalizeOne(final Mono<User> mono) {
	    mono.subscribe(System.out::println);
		return mono.map(person -> new User(
		        person.getUsername().toUpperCase(),
		        person.getFirstname().toUpperCase(),
		        person.getLastname().toUpperCase())
		        );
	}

//========================================================================================

	@Test
	public void transformFlux() {
		final Flux<User> flux = repository.findAll();
		TestSubscriber
				.subscribe(capitalizeMany(flux))
				.await()
				.assertValues(
					new User("SWHITE", "SKYLER", "WHITE"),
					new User("JPINKMAN", "JESSE", "PINKMAN"),
					new User("WWHITE", "WALTER", "WHITE"),
					new User("SGOODMAN", "SAUL", "GOODMAN"))
				.assertComplete();
	}

	// TODO Capitalize the users username, firstName and lastName
	Flux<User> capitalizeMany(final Flux<User> flux) {
		return flux.map(user -> new User(
		            user.getUsername().toUpperCase(),
		            user.getFirstname().toUpperCase(),
		            user.getLastname().toUpperCase()
		            )
		);
	}

//========================================================================================

	@Test
	public void  asyncTransformFlux() {
		final Flux<User> flux = repository.findAll();
		TestSubscriber
				.subscribe(asyncCapitalizeMany(flux))
				.await()
				.assertValues(
					new User("SWHITE", "SKYLER", "WHITE"),
					new User("JPINKMAN", "JESSE", "PINKMAN"),
					new User("WWHITE", "WALTER", "WHITE"),
					new User("SGOODMAN", "SAUL", "GOODMAN"))
				.assertComplete();
	}

	// TODO Capitalize the users username, firstName and lastName using asyncCapitalizeUser()
	Flux<User> asyncCapitalizeMany(final Flux<User> flux) {
		return flux.flatMap(user -> asyncCapitalizeUser(user));
	}

	Mono<User> asyncCapitalizeUser(final User u) {
		return Mono.just(new User(u.getUsername().toUpperCase(), u.getFirstname().toUpperCase(), u.getLastname().toUpperCase()));
	}

}
