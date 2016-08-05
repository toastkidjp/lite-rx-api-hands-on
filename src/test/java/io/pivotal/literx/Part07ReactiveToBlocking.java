package io.pivotal.literx;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Learn how to turn Reactive API to blocking one.
 *
 * @author Sebastien Deleuze
 */
public class Part07ReactiveToBlocking {

	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void mono() {
		final Mono<User> mono = repository.findFirst();
		final User user = monoToValue(mono);
		assertEquals(User.SKYLER, user);
	}

	// TODO Return the user contained in that Mono
	User monoToValue(final Mono<User> mono) {
		return mono.block();
	}

//========================================================================================

	@Test
	public void flux() {
		final Flux<User> flux = repository.findAll();
		final Iterable<User> users = fluxToValues(flux);
		final Iterator<User> it = users.iterator();
		assertEquals(User.SKYLER, it.next());
		assertEquals(User.JESSE, it.next());
		assertEquals(User.WALTER, it.next());
		assertEquals(User.SAUL, it.next());
		assertFalse(it.hasNext());
	}

	// TODO Return the users contained in that Flux
	Iterable<User> fluxToValues(final Flux<User> flux) {
		return flux.toIterable();
	}

}
