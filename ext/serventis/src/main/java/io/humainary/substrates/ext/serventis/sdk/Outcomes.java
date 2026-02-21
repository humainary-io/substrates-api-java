// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign.FAIL;
import static io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign.SUCCESS;

/// # Outcomes API
///
/// The `Outcomes` API provides a minimal binary verdict vocabulary for expressing
/// success or failure. It enables systems to emit universal SUCCESS/FAIL signals
/// that aggregate across diverse domain vocabularies.
///
/// ## Purpose
///
/// This API offers the smallest possible outcome vocabulary for universal rollups
/// and translations. When domain APIs (Services, Tasks, Transactions, etc.) have
/// rich outcome vocabularies (COMPLETE, EXPIRE, TIMEOUT, REJECT, etc.), observers
/// can emit a simple binary verdict using this API to drive higher-level translations
/// (Statuses, Situations) without requiring knowledge of source vocabularies.
///
/// ## Key Insight: Semiotic Ascent
///
/// As signals ascend the semiotic hierarchy, they trade specificity for universality.
/// Outcomes represents this abstraction: the cause is lost, the verdict remains.
/// If you need cause-level detail, subscribe to the domain API directly.
///
/// ## Key Concepts
///
/// - **Outcome**: An instrument that emits binary SUCCESS/FAIL verdicts
/// - **Sign**: The verdict classification (SUCCESS, FAIL)
///
/// ## Usage Example
///
/// ```java
/// // Create an outcome instrument
/// var outcome = circuit
///     .conduit(Outcomes::composer)
///     .percept(cortex.name("payment.outcomes"));
///
/// // Emit verdicts
/// outcome.success();  // operation succeeded
/// outcome.fail();     // operation failed
/// ```
///
/// ## Relationship to Other APIs
///
/// - **Domain APIs**: Provide rich outcome vocabularies (SUCCESS/FAIL/EXPIRE/TIMEOUT/etc.)
/// - **Outcomes**: Aggregates to binary verdict - did it work?
/// - **Statuses/Situations**: Consume outcome streams for condition/urgency translation
///
/// ## When to Use
///
/// Use Outcomes when you need:
/// - Cross-vocabulary success/failure aggregation
/// - Simple success rate metrics
/// - Binary health signals for Status translation
///
/// If you need to know *why* something failed, subscribe to the domain API.
/// Outcomes answers one question: **did it work?**
///
/// @author William David Louth
/// @since 1.0

public final class Outcomes
  implements Serventis {

  private Outcomes () { }

  /// A static composer function for creating Outcome instruments.
  ///
  /// This method can be used as a method reference with conduits:
  ///
  /// Example usage:
  /// ```java
  /// var outcome = circuit.conduit(Outcomes::composer).percept(cortex.name("svc.outcomes"));
  /// ```
  ///
  /// @param channel the channel from which to create the outcome instrument
  /// @return a new Outcome instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Outcome composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Outcome (
        channel.pipe ()
      );

  }


  /// A [Sign] represents the binary verdict of an operation.
  ///
  /// These signs form the minimal success/failure vocabulary. They enable
  /// universal aggregation across diverse domain APIs without requiring
  /// knowledge of source-specific outcome vocabularies.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the operation succeeded.
    ///
    /// Emitted when an operation completes successfully, regardless of
    /// the domain-specific success vocabulary (COMPLETE, COMMIT, GRANT, etc.).

    SUCCESS,

    /// Indicates the operation failed.
    ///
    /// Emitted when an operation fails, regardless of the domain-specific
    /// failure vocabulary (FAIL, TIMEOUT, EXPIRE, REJECT, DENY, ABORT, etc.).

    FAIL

  }


  /// The [Outcome] class emits binary verdict signals.
  ///
  /// ## Usage
  ///
  /// Use the semantic methods: `outcome.success()`, `outcome.fail()`
  ///
  /// Outcomes provide the simplest possible verdict vocabulary for
  /// cross-domain aggregation and status translation.

  @Queued
  @Provided
  public static final class Outcome
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Outcome (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a FAIL verdict.

    public void fail () {

      pipe.emit (
        FAIL
      );

    }

    /// Emits the specified sign.
    ///
    /// @param sign the sign to emit

    @Override
    public void sign (
      @NotNull final Sign sign
    ) {

      pipe.emit (
        sign
      );

    }

    /// Emits a SUCCESS verdict.

    public void success () {

      pipe.emit (
        SUCCESS
      );

    }

  }

}
