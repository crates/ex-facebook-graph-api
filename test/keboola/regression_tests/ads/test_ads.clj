(ns keboola.regression-tests.ads.test-ads
  (:require [clj-http.fake :refer :all]
            [clojure.test :as t :refer :all]
            [keboola.facebook.extractor.core :refer [prepare-and-run]]
            [keboola.facebook.extractor.output :refer [reset-columns-map]]
            [keboola.facebook.extractor.sync-actions :refer [disable-log-token]]
            [keboola.regression-tests.ads.apicalls :as apicalls]
            [keboola.regression-tests.outdirs-check :as outdirs-check]
            [keboola.test-utils.core :as test-utils]))

(deftest ads-test
  (let [tmp-dir (.getPath (test-utils/mk-tmp-dir! "ads"))]
    (disable-log-token)
    (println "testing dir:" tmp-dir)
    (println "expected dir:" "test/keboola/regression_tests/ads")
    (test-utils/copy-config-tmp "test/keboola/regression_tests/ads" tmp-dir)
    (with-global-fake-routes-in-isolation
      apicalls/recorded
      (reset-columns-map)
      (prepare-and-run tmp-dir)
      (outdirs-check/is-equal "test/keboola/regression_tests/ads" tmp-dir)
      )))
