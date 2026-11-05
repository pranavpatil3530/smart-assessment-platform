import { useState, useEffect, useRef } from 'react';

/**
 * High-precision countdown timer hook based on target endTime timestamp.
 * Prevents timer manipulation from browser refreshes.
 *
 * @param {string|Date} endTime - The ISO deadline or timestamp
 * @param {function} onExpire - Callback executed when timer reaches 0
 */
export const useCountdownTimer = (endTime, onExpire) => {
  const [secondsRemaining, setSecondsRemaining] = useState(() => {
    if (!endTime) return 0;
    const diff = Math.floor((new Date(endTime).getTime() - Date.now()) / 1000);
    return Math.max(0, diff);
  });

  const onExpireRef = useRef(onExpire);
  onExpireRef.current = onExpire;

  const expiredTriggeredRef = useRef(false);

  useEffect(() => {
    if (!endTime) return;

    const calculateRemaining = () => {
      const diff = Math.floor((new Date(endTime).getTime() - Date.now()) / 1000);
      const remaining = Math.max(0, diff);
      setSecondsRemaining(remaining);

      if (remaining <= 0 && !expiredTriggeredRef.current) {
        expiredTriggeredRef.current = true;
        if (onExpireRef.current) {
          onExpireRef.current();
        }
      }
    };

    calculateRemaining();
    const intervalId = setInterval(calculateRemaining, 1000);

    return () => clearInterval(intervalId);
  }, [endTime]);

  const formatTime = () => {
    const hours = Math.floor(secondsRemaining / 3600);
    const minutes = Math.floor((secondsRemaining % 3600) / 60);
    const seconds = secondsRemaining % 60;

    const pad = (n) => String(n).padStart(2, '0');

    if (hours > 0) {
      return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
    }
    return `${pad(minutes)}:${pad(seconds)}`;
  };

  const isWarning = secondsRemaining > 0 && secondsRemaining <= 300; // < 5 mins
  const isCritical = secondsRemaining > 0 && secondsRemaining <= 60; // < 1 min

  return {
    secondsRemaining,
    formattedTime: formatTime(),
    isWarning,
    isCritical,
    isExpired: secondsRemaining <= 0,
  };
};
