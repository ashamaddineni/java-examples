package io.mincong.immutables;

import org.immutables.value.Value;

/**
 * Geographical point (implementation is generated by framework: Immutables)
 *
 * @author Mincong Huang
 */
@Value.Immutable
public abstract class GeoPoint {

  public static GeoPoint of(double longitude, double latitude) {
    return ImmutableGeoPoint.of(longitude, latitude);
  }

  @Value.Parameter
  public abstract double longitude();

  @Value.Parameter
  public abstract double latitude();
}
