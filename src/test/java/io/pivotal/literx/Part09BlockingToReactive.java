package io.pivotal.literx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.BlockingRepository;
import io.pivotal.literx.repository.BlockingUserRepository;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.TestSubscriber;

/**
 * Learn how to call blocking code from Reactive one with adapted concurrency strategy for
 * blocking code that produces or receives data.
 *
 * For those who know RxJava:
 *  - RxJava subscribeOn = Reactor subscribeOn
 *  - RxJava observeOn = Reactor publishOn
 *
 * @author Sebastien Deleuze
 * @see Flux#subscribeOn(Scheduler)
 * @see Flux#publishOn(Scheduler)
 * @see Schedulers
 */
public class Part09BlockingToReactive {

//========================================================================================

	@Test
	public void slowPublisherFastSubscriber() {
		final BlockingUserRepository repository = new BlockingUserRepository();
		final Flux<User> flux = blockingRepositoryToFlux(repository);
		assertEquals(0, repository.getCallCount());
		TestSubscriber
				.subscribe(flux)
				.assertNotTerminated()
				.await()
				.assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.assertComplete();
	}

	// TODO Create a Flux for reading all users from the blocking repository, and run it with an elastic scheduler
	Flux<User> blockingRepositoryToFlux(final BlockingRepository<User> repository) {
		return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
		        .subscribeOn(Schedulers.elastic());
	}

//========================================================================================

	@Test
	public void fastPublisherSlowSubscriber() {
		final ReactiveRepository<User> reactiveRepository = new ReactiveUserRepository();
		final BlockingRepository<User> blockingRepository = new BlockingUserRepository(new User[]{});
		final Mono<Void> complete = fluxToBlockingRepository(reactiveRepository.findAll(), blockingRepository);
		TestSubscriber
				.subscribe(complete)
				.assertNotTerminated()
				.await()
				.assertComplete();
		final Iterator<User> it = blockingRepository.findAll().iterator();
		assertEquals(User.SKYLER, it.next());
		assertEquals(User.JESSE, it.next());
		assertEquals(User.WALTER, it.next());
		assertEquals(User.SAUL, it.next());
		assertFalse(it.hasNext());
	}

	// TODO Insert users contained in the Flux parameter in the blocking repository using a parallel scheduler
	Mono<Void> fluxToBlockingRepository(final Flux<User> flux, final BlockingRepository<User> repository) {
		return flux.publishOn(Schedulers.parallel())
		           .doOnNext(user -> repository.save(user))
		           .then();
	}

//========================================================================================

	@Test
	public void nullHandling() {
		Mono<User> mono = nullAwareUserToMono(User.SKYLER);
		TestSubscriber
				.subscribe(mono)
				.assertValues(User.SKYLER)
				.assertComplete();
		mono = nullAwareUserToMono(null);
		TestSubscriber
				.subscribe(mono)
				.assertNoValues()
				.assertComplete();
	}

	// TODO Return a valid Mono of user for null input and non null input user (hint: Reactive Streams does not accept null values)
	Mono<User> nullAwareUserToMono(final User user) {
		return Mono.justOrEmpty(user);
	}

}
