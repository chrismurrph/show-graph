(ns layout.math)

(defn abs [^double n]
  (Math/abs n))

(defn log [^double n]
  (Math/log n))

(defn pow [^double n ^double m]
  (Math/pow n m))

(defn atanh [^double value]
  ;Math.log(Math.abs((value + 1.0) / (1.0 - value))) / 2;
  (assert (not= 1.0 value) ["Will be a / by 0 in atanh" value])
  (/ (log
       (abs
         (/ (+ value 1.0)
            (- 1.0 value))))
     2))

(defn signum [^double n]
  (Math/signum n))

(defn sqrt [^double n]
  (Math/sqrt n))

(defn copy-sign [^double n ^double m]
  (Math/copySign n m))

(defn radians->degrees [radian]
  (* radian (/ 180 Math/PI)))

(defn atan2 [^double n ^double m]
  (Math/atan2 n m))

(defn line-slope [[xa ya] [xb yb]]
  (let [[x y :as origin-based] [(- xb xa) (- yb ya)]
        radians-polar-angle (atan2 y x)]
    (radians->degrees radians-polar-angle)))

