package io.pivotal.literx;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

/**
 * Learn how to use various other operators.
 *
 * @author Sebastien Deleuze
 */
public class Part06OtherOperations {

	final static User MARIE = new User("mschrader", "Marie", "Schrader");
	final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");

//========================================================================================

	@Test
	public void zipFirstNameAndLastName() {
		final Flux<String> usernameFlux = Flux.just(User.SKYLER.getUsername(), User.JESSE.getUsername(), User.WALTER.getUsername(), User.SAUL.getUsername());
		final Flux<String> firstnameFlux = Flux.just(User.SKYLER.getFirstname(), User.JESSE.getFirstname(), User.WALTER.getFirstname(), User.SAUL.getFirstname());
		final Flux<String> lastnameFlux = Flux.just(User.SKYLER.getLastname(), User.JESSE.getLastname(), User.WALTER.getLastname(), User.SAUL.getLastname());
		final Flux<User> userFlux = userFluxFromStringFlux(usernameFlux, firstnameFlux, lastnameFlux);
		TestSubscriber
				.subscribe(userFlux)
				.assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL);
	}

	// TODO Create a Flux of user from Flux of username, firstname and lastname.
	Flux<User> userFluxFromStringFlux(final Flux<String> usernameFlux, final Flux<String> firstnameFlux, final Flux<String> lastnameFlux) {
		return Flux.zip(usernameFlux, firstnameFlux, lastnameFlux)
		        .map(tuple3 -> new User(tuple3.t1, tuple3.t2, tuple3.t3));
	}

//========================================================================================

	@Test
	public void fastestMono() {
		ReactiveRepository<User> repository1 = new ReactiveUserRepository(MARIE);
		ReactiveRepository<User> repository2 = new ReactiveUserRepository(250, MIKE);
		Mono<User> mono = useFastestMono(repository1.findFirst(), repository2.findFirst());
		TestSubscriber
				.subscribe(mono)
				.await()
				.assertValues(MARIE)
				.assertComplete();

		repository1 = new ReactiveUserRepository(250, MARIE);
		repository2 = new ReactiveUserRepository(MIKE);
		mono = useFastestMono(repository1.findFirst(), repository2.findFirst());
		TestSubscriber
				.subscribe(mono)
				.await()
				.assertValues(MIKE)
				.assertComplete();
	}

	// TODO return the mono which returns faster its value
	Mono<User> useFastestMono(final Mono<User> mono1, final Mono<User> mono2) {
		return Mono.first(mono1, mono2);
	}

//========================================================================================

	@Test
	public void fastestFlux() {
		ReactiveRepository<User> repository1 = new ReactiveUserRepository(MARIE, MIKE);
		ReactiveRepository<User> repository2 = new ReactiveUserRepository(250);
		Flux<User> flux = useFastestFlux(repository1.findAll(), repository2.findAll());
		TestSubscriber
				.subscribe(flux)
				.await()
				.assertValues(MARIE, MIKE)
				.assertComplete();

		repository1 = new ReactiveUserRepository(250, MARIE, MIKE);
		repository2 = new ReactiveUserRepository();
		flux = useFastestFlux(repository1.findAll(), repository2.findAll());
		TestSubscriber
				.subscribe(flux)
				.await()
				.assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.assertComplete();
	}

	// TODO return the flux which returns faster the first value
	Flux<User> useFastestFlux(final Flux<User> flux1, final Flux<User> flux2) {
		return Flux.firstEmitting(flux1, flux2);
	}

//========================================================================================

	@Test
	public void end() {
		final ReactiveRepository<User> repository = new ReactiveUserRepository();
		final Mono<Void> end = endOfFlux(repository.findAll());
		TestSubscriber
				.subscribe(end)
				.assertNotTerminated()
				.await()
				.assertNoValues()
				.assertComplete();
	}

	// TODO Convert the input Flux<User> to a Mono<Void> that represents the complete signal of the flux
	Mono<Void> endOfFlux(final Flux<User> flux) {
		return flux.then();
	}

//========================================================================================

	@Test
	public void monoWithValueInsteadOfError() {
		Mono<User> mono = betterCallSaulForBogusMono(Mono.error(new IllegalStateException()));
		TestSubscriber
				.subscribe(mono)
				.assertValues(User.SAUL)
				.assertComplete();

		mono = betterCallSaulForBogusMono(Mono.just(User.SKYLER));
		TestSubscriber
				.subscribe(mono)
				.assertValues(User.SKYLER)
				.assertComplete();
	}

	// TODO Return a Mono<User> containing Saul when an error occurs in the input Mono, else do not change the input Mono.
	Mono<User> betterCallSaulForBogusMono(final Mono<User> mono) {
		return mono.otherwise(e -> Mono.just(User.SAUL));
	}

//========================================================================================

	@Test
	public void fluxWithValueInsteadOfError() {
		Flux<User> flux = betterCallSaulForBogusFlux(Flux.error(new IllegalStateException()));
		TestSubscriber
				.subscribe(flux)
				.assertValues(User.SAUL)
				.assertComplete();

		flux = betterCallSaulForBogusFlux(Flux.just(User.SKYLER, User.WALTER));
		TestSubscriber
				.subscribe(flux)
				.assertValues(User.SKYLER, User.WALTER)
				.assertComplete();
	}

	// TODO Return a Flux<User> containing Saul when an error occurs in the input Flux, else do not change the input Flux.
	Flux<User> betterCallSaulForBogusFlux(final Flux<User> flux) {
		return flux.onErrorReturn(User.SAUL);
	}

}
