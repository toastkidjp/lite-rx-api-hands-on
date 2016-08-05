package io.pivotal.literx;

import org.junit.Test;

import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

/**
 * Learn how to create Mono instances.
 *
 * @author Sebastien Deleuze
 * @see <a href="http://projectreactor.io/core/docs/api/reactor/core/publisher/Mono.html">Mono Javadoc</a>
 * @see <a href="http://projectreactor.io/core/docs/api/reactor/core/test/TestSubscriber.html">TestSubscriber Javadoc</a>
 */
public class Part02CreateMono {

//========================================================================================

	@Test
	public void empty() {
		final Mono<String> mono = emptyMono();
		TestSubscriber
				.subscribe(mono)
				.assertValueCount(0)
				.assertComplete();
	}

	// TODO Return an empty Mono
	Mono<String> emptyMono() {
		return Mono.empty();
	}

//========================================================================================

	@Test
	public void fromValue() {
		final Mono<String> mono = fooMono();
		TestSubscriber
				.subscribe(mono)
				.assertValues("foo")
				.assertComplete();
	}

	// TODO Return a Mono that contains a "foo" value
	Mono<String> fooMono() {
		return Mono.just("foo");
	}

//========================================================================================

	@Test
	public void error() {
		final Mono<String> mono = errorMono();
		TestSubscriber
				.subscribe(mono)
				.assertError(IllegalStateException.class)
				.assertNotComplete();
	}

	// TODO Create a Mono that emits an IllegalStateException
	Mono<String> errorMono() {
		return Mono.error(new IllegalStateException());
	}

}
