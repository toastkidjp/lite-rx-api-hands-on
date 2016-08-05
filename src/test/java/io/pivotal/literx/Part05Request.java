package io.pivotal.literx;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.test.TestSubscriber;
/**
 * Learn how to control the demand.
 *
 * @author Sebastien Deleuze
 */
public class Part05Request {

	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void requestNoValue() {
		final Flux<User> flux = repository.findAll();
		final TestSubscriber<User> testSubscriber = createSubscriber(flux);
		testSubscriber
				.await()
				.assertNoValues();
	}

	// TODO Create a TestSubscriber that requests initially no value
	TestSubscriber<User> createSubscriber(final Flux<User> flux) {
		return TestSubscriber.subscribe(flux, 0);
	}

//========================================================================================

	@Test
	public void requestValueOneByOne() {
		final Flux<User> flux = repository.findAll();
		final TestSubscriber<User> testSubscriber = createSubscriber(flux);
		testSubscriber
				.assertValueCount(0);
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SKYLER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.JESSE)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.WALTER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SAUL)
				.assertComplete();
	}

	// TODO Request one value
	void requestOne(final TestSubscriber<User> testSubscriber) {
	    testSubscriber.request(1L);
	}

//========================================================================================

	@Test
	public void experimentWithLog() {
	    final Flux<User> flux = fluxWithLog();
		final TestSubscriber<User> testSubscriber = createSubscriber(flux);
		testSubscriber
				.assertValueCount(0);
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SKYLER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.JESSE)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.WALTER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SAUL)
				.assertComplete();
	}

	// TODO Return a Flux with all users stored in the repository that prints automatically logs for all Reactive Streams signals
	Flux<User> fluxWithLog() {
	    return repository.findAll().log();
	}


//========================================================================================

	@Test
	public void experimentWithDoOn() {
		final Flux<User> flux = fluxWithDoOnPrintln();
		final TestSubscriber<User> testSubscriber = createSubscriber(flux);
		testSubscriber
				.assertValueCount(0);
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SKYLER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.JESSE)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.WALTER)
				.assertNotTerminated();
		requestOne(testSubscriber);
		testSubscriber
				.awaitAndAssertNextValues(User.SAUL)
				.assertComplete();
	}

	// TODO Return a Flux with all users stored in the repository that prints "Starring:" on subscribe, "firstname lastname" for all values and "The end!" on complete
	Flux<User> fluxWithDoOnPrintln() {
		return repository.findAll()
		        .doOnSubscribe(sub -> System.out.println("Starring:"))
		        .doOnNext(user -> System.out.println(
		                String.format("%s %s", user.getFirstname(), user.getLastname())))
		        .doOnComplete(() -> System.out.println("The end!"));
	}

}
