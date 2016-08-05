/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.literx;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import reactor.adapter.RxJava1Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;
import rx.Observable;
import rx.Single;

/**
 * Learn how to convert from/to Java 8+ CompletableFuture, RxJava Observable/Single and
 * Reactor Stream.
 *
 * Mono and Flux already implements Reactive Streams interfaces so they are natively
 * Reactive Streams compliant + there are Mono.from(Publisher) and Flux.from(Publisher)
 * factory methods.
 *
 * @author Sebastien Deleuze
 */
public class Part08Conversion {

	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void observableConversion() {
		final Flux<User> flux = repository.findAll();
		final Observable<User> observable = fromFluxToObservable(flux);
		TestSubscriber
				.subscribe(fromObservableToFlux(observable))
				.await()
				.assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.assertComplete();
	}

	// TODO Convert Flux to RxJava Observable thanks to a Reactor converter
	Observable<User> fromFluxToObservable(final Flux<User> flux) {
		return RxJava1Adapter.publisherToObservable(flux);
	}

	// TODO Convert RxJava Observable to Flux thanks to a Reactor converter
	Flux<User> fromObservableToFlux(final Observable<User> observable) {
		return RxJava1Adapter.observableToFlux(observable);
	}

//========================================================================================

	@Test
	public void singleConversion() {
		final Mono<User> mono = repository.findFirst();
		final Single<User> single = fromMonoToSingle(mono);
		TestSubscriber
				.subscribe(fromSingleToMono(single))
				.await()
				.assertValues(User.SKYLER)
				.assertComplete();
	}

	// TODO Convert Mono to RxJava Single thanks to a Reactor converter
	Single<User> fromMonoToSingle(final Mono<User> mono) {
		return RxJava1Adapter.publisherToSingle(mono);
	}

	// TODO Convert RxJava Single to Mono thanks to a Reactor converter
	Mono<User> fromSingleToMono(final Single<User> single) {
		return RxJava1Adapter.singleToMono(single);
	}

//========================================================================================

	@Test
	public void completableFutureConversion() {
		final Mono<User> mono = repository.findFirst();
		final CompletableFuture<User> future = fromMonoToCompletableFuture(mono);
		TestSubscriber
				.subscribe(fromCompletableFutureToMono(future))
				.await()
				.assertValues(User.SKYLER)
				.assertComplete();
	}

	// TODO Convert Mono to Java 8+ CompletableFuture thanks to a Reactor converter
	CompletableFuture<User> fromMonoToCompletableFuture(final Mono<User> mono) {
		return mono.toFuture();
	}

	// TODO Convert Java 8+ CompletableFuture to Mono thanks to a Reactor converter
	Mono<User> fromCompletableFutureToMono(final CompletableFuture<User> future) {
		return Mono.fromFuture(future);
	}


}
