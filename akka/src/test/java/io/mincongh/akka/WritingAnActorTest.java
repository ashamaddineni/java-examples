package io.mincongh.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.*;

/**
 * @author Mincong Huang
 * @blog https://mincong.io/2020/06/20/akka-actor/
 */
class WritingAnActorTest {

  private ActorSystem system;
  private TestKit probe;

  @BeforeEach
  void setUp() {
    system = ActorSystem.create();
    probe = new TestKit(system);
  }

  @AfterEach
  void tearDown() {
    TestKit.shutdownActorSystem(system);
  }

  @Test
  void subscribeAndUnsubscribe() {
    // Given an actor under test
    var actor = system.actorOf(UserSubscriptionActor.props());

    // When asking to subscribe
    actor.tell(new Subscribe("Foo"), probe.getRef());

    // Then subscription is successful
    probe.expectMsg("Subscription succeed for user Foo");

    // When asking to unsubscribe
    actor.tell(new Unsubscribe("Foo"), probe.getRef());

    // Then un-subscription is successful
    probe.expectMsg("User Foo unsubscribed");
  }

  @Test
  void listAllSubscriptions() {
    // Given an actor under test with two users subscribed
    var users = new HashSet<>(Set.of("Bar", "Foo"));
    var props = UserSubscriptionActor.props(users);
    var actor = system.actorOf(props);

    // When listing all the subscriptions
    actor.tell("list-subscriptions", probe.getRef());

    // Then the response is correct
    probe.expectMsg("Bar, Foo");
  }

  @Test
  void unknownMessage() {
    // Given an actor under test
    var actor = system.actorOf(UserSubscriptionActor.props());

    // When sending unknown message
    actor.tell("hello?", probe.getRef());

    // Then the response is correct
    probe.expectMsg("Unknown message: hello?");
  }

  @Test
  @Disabled("Don't create an instance via constructor (new), use actorOf(...)")
  void doNotUseConstructorDirectly() {
    /*
     * akka.actor.ActorInitializationException: You cannot create an instance
     * of [io.mincongh.akka.WritingAnActorTest$UserSubscriptionActor]
     * explicitly using the constructor (new). You have to use one of the
     * 'actorOf' factory methods to create a new actor. See the documentation.
     */
    new UserSubscriptionActor(new HashSet<>());
  }

  static class Subscribe {
    final String userId;

    Subscribe(String userId) {
      this.userId = userId;
    }
  }

  static class Unsubscribe {
    final String userId;

    Unsubscribe(String userId) {
      this.userId = userId;
    }
  }

  static class UserSubscriptionActor extends AbstractActor {

    private final Set<String> subscribedUsers;

    private UserSubscriptionActor(Set<String> subscribedUsers) {
      this.subscribedUsers = subscribedUsers;
    }

    public static Props props() {
      return Props.create(
          UserSubscriptionActor.class, () -> new UserSubscriptionActor(new HashSet<>()));
    }

    public static Props props(Set<String> subscribedUsers) {
      return Props.create(
          UserSubscriptionActor.class, () -> new UserSubscriptionActor(subscribedUsers));
    }

    @Override
    public Receive createReceive() {
      return receiveBuilder()
          .match(Subscribe.class, this::onSubscribe)
          .match(Unsubscribe.class, this::onUnsubscribe)
          .matchEquals("list-subscriptions", this::onList)
          .matchAny(this::onUnknown)
          .build();
    }

    private void onSubscribe(Subscribe subscription) {
      String reply;
      var isNew = subscribedUsers.add(subscription.userId);
      if (isNew) {
        reply = "Subscription succeed for user " + subscription.userId;
      } else {
        reply = "User " + subscription.userId + " already subscribed";
      }
      /*
       * Reply to the sender by sending a message. Here, sender means the actor
       * which sent of the latest message received by this actor. Note that the
       * sender is the reference of the actor (`ActorRef`), not the actor
       * itself.
       *
       * A typical pattern is:
       *
       *    sender().tell(reply, self())
       */
      sender().tell(reply, self());
    }

    private void onUnsubscribe(Unsubscribe unsubscribe) {
      var hasUser = subscribedUsers.remove(unsubscribe.userId);
      if (hasUser) {
        sender().tell("User " + unsubscribe.userId + " unsubscribed", self());
      } else {
        sender().tell("User " + unsubscribe.userId + " not found", self());
      }
    }

    private void onList(String ignore) {
      String s = String.join(", ", new TreeSet<>(subscribedUsers));
      sender().tell(s, self());
    }

    private void onUnknown(Object unknown) {
      sender().tell("Unknown message: " + unknown, self());
    }
  }
}
