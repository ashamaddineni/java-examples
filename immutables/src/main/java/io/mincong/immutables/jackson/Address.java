package io.mincong.immutables.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Address (implementation is generated by framework: Immutables)
 *
 * @author Mincong Huang
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAddress.class)
@JsonDeserialize(as = ImmutableAddress.class)
public interface Address {

  String address();

  String city();

  @JsonProperty("zipcode")
  String postalCode();

}
