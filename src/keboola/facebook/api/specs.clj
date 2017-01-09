(ns keboola.facebook.api.specs
  (:require [clojure.spec :as s]
            [clojure.string :refer [blank?]]
            [clojure.test.check.generators :as gen]))

(defn not-blank? [s]  ((complement blank?) s))
(s/def ::key (s/and string? not-blank?))

(s/def ::table-value (s/or :int int? :string string?))
(s/def ::id (s/and string? (complement blank?)))
(s/def ::fb-graph-node string?)
(s/def ::parent-id ::id)
(s/def ::table-name string?)

(s/def ::keboola (s/keys :req-un [::parent-id ::fb-graph-node ::table-name]))
(s/def ::fb-table-row-core (s/keys :req-un [::id ::keboola]))

(s/def ::fb-scalars (s/every-kv keyword? ::table-value :max-count 5))

(s/def ::fb-table-row
  (s/with-gen
    (s/or ::fb-scalars ::fb-table-row-core)
    #(gen/fmap (partial apply into) (gen/tuple (s/gen ::fb-table-row-core) (s/gen ::fb-scalars)))))

(s/def ::simple-object (s/map-of keyword? ::table-value :max-count 5))
(s/def ::complex-object (s/map-of keyword? (s/or :scalar ::table-value :object ::simple-object ) :min-count 1 :max-count 5))
(s/def ::multi-complex-object (s/map-of keyword? (s/or :scalar ::table-value :object ::complex-object)))

(s/def :metric/key1 string?)
(s/def :metric/key2 string?)
(s/def :metric/value ::table-value)
(s/def ::key1-key2-value (s/keys :req-un [:metric/key1 :metric/key2 :metric/value]))
(s/def ::end_time string?)

(s/def :insights/value ::complex-object)
(s/def ::insights (s/keys :req-un [:insights/value ::end_time]))

(s/def ::nested-object (s/tuple keyword? (s/keys :req-un [::data])))
(s/def ::fb-value (s/or :scalar (s/tuple keyword? ::table-value)
                        :nested ::nested-object
                        :key-values-object ::simple-object))

(s/def ::fb-object (s/coll-of ::fb-value :into {} :max-count 3 :min-count 1))
(s/def ::data (s/coll-of ::fb-object
                         :min-count 1
                         :max-count 6))



(s/def ::columns (s/every-kv string?
                             (s/coll-of (s/or :string string? :keyword keyword?) :distinct true)))

(s/def parent-types (s/coll-of ::fb-graph-node :kind set? :distinct true))

(s/def ::cnt pos-int?)

#_(s/exercise (s/coll-of (s/or :fb-scalar (s/tuple keyword? int?) :other (s/keys :req-un [::data])) :into {}) 5)
