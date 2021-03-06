package org.factcast.core.subscription;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.factcast.core.spec.FactSpec;
import org.factcast.core.util.FactCastJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Implementation of {@link SubscriptionRequest}, that is supposed to be used
 * when transfered on the wire to a remote store (for instance via GRPC or REST)
 * 
 * Note that FactSpec.forMark() is silently added to the list of specifications,
 * if marks is true.
 * 
 * @author uwe.schaefer@mercateo.com
 *
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonIgnoreProperties
public class SubscriptionRequestTO implements SubscriptionRequest {

    @JsonProperty
    long maxBatchDelayInMs = 0;

    @JsonProperty
    boolean marks;

    @JsonProperty
    boolean continous;

    @JsonProperty
    boolean ephemeral;

    @JsonProperty
    boolean idOnly = false;

    @JsonProperty
    UUID startingAfter;

    @JsonProperty
    String debugInfo;

    @JsonProperty
    final List<FactSpec> specs = new LinkedList<>();

    public SubscriptionRequestTO() {
    }

    public boolean hasAnyScriptFilters() {
        return specs.stream().anyMatch(s -> s.jsFilterScript() != null);
    }

    public java.util.Optional<UUID> startingAfter() {
        return java.util.Optional.ofNullable(startingAfter);
    }

    // copy constr. from a SR
    public SubscriptionRequestTO(SubscriptionRequest request) {
        maxBatchDelayInMs = request.maxBatchDelayInMs();
        continous = request.continous();
        ephemeral = request.ephemeral();
        startingAfter = request.startingAfter().orElse(null);
        debugInfo = request.debugInfo();
        specs.addAll(request.specs());
        marks = request.marks();
    }

    public static SubscriptionRequestTO forFacts(SubscriptionRequest request) {
        SubscriptionRequestTO t = new SubscriptionRequestTO(request);
        t.idOnly(false);
        return t;
    }

    public static SubscriptionRequestTO forIds(SubscriptionRequest request) {
        SubscriptionRequestTO t = new SubscriptionRequestTO(request);
        t.idOnly(true);
        return t;
    }

    public void addSpecs(@NonNull List<FactSpec> factSpecs) {
        checkArgument(!factSpecs.isEmpty());
        specs.addAll(factSpecs);
    }

    @Override
    public List<FactSpec> specs() {
        ArrayList<FactSpec> l = Lists.newArrayList(specs);
        if (marks) {
            l.add(0, FactSpec.forMark());
        }
        return Collections.unmodifiableList(l);
    }

    public String dump() {
        return FactCastJson.writeValueAsString(this);
    }

    @Override
    public String toString() {
        return debugInfo;
    }
}
