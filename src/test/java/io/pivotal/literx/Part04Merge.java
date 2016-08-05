package io.pivotal.literx;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

/**
 * Learn how to merge flux.
 *
 * @author Sebastien Deleuze
 */
public class Part04Merge {

	final static User MARIE = new User("mschrader", "Marie", "Schrader");
	final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");

	ReactiveRepository<User> repository1 = new ReactiveUserRepository(500);
	ReactiveRepository<User> repository2 = new ReactiveUserRepository(MARIE, MIKE);

//========================================================================================

	@Test
	public void mergeWithInterleave() {
		final Flux<User> flux = mergeFluxWithInterleave(repository1.findAll(), repository2.findAll());
		TestSubscriber
				.subscribe(flux)
				.await()
				.assertValues(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.assertComplete();
	}

	// TODO Merge flux1 and flux2 values with interleave
	Flux<User> mergeFluxWithInterleave(final Flux<User> flux1, final Flux<User> flux2) {
		return Flux.merge(flux1, flux2);
	}

//========================================================================================

	@Test
	public void mergeWithNoInterleave() {
		final Flux<User> flux = mergeFluxWithNoInterleave(repository1.findAll(), repository2.findAll());
		TestSubscriber
				.subscribe(flux)
				.await()
				.assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
				.assertComplete();
	}

	// TODO Merge flux1 and flux2 values with no interleave (flux1 values, and then flux2 values)
	Flux<User> mergeFluxWithNoInterleave(final Flux<User> flux1, final Flux<User> flux2) {
		return Flux.concat(flux1, flux2);
	}

//========================================================================================

	@Test
	public void multipleMonoToFlux() {
		final Mono<User> skylerMono = repository1.findFirst();
		final Mono<User> marieMono = repository2.findFirst();
		final Flux<User> flux = createFluxFromMultipleMono(skylerMono, marieMono);
		TestSubscriber
				.subscribe(flux)
				.await()
				.assertValues(User.SKYLER, MARIE)
				.assertComplete();
	}

	// TODO Create a Flux containing the values of the 2 Mono
	Flux<User> createFluxFromMultipleMono(final Mono<User> mono1, final Mono<User> mono2) {
		return Flux.concat(mono1, mono2);//mono1.concatWith(mono2);
	}

}
