import { useEffect, useRef } from 'react';

/**
 * Custom hook to call a function after a delay
 * @param callback is the function to be called after the delay
 * @param delay is the time in milliseconds to wait before calling the callback function
 * @see https://react.dev/reference/react/useEffect#wrapping-effects-in-custom-hooks
 * @see https://usehooks-ts.com/react-hook/use-interval
 */
export function useInterval(callback: () => void, delay: number | null) {
  const savedCallback = useRef(callback);

  // Remember the latest callback if it changes.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    // Don't schedule if no delay is specified.
    if (!delay && delay !== 0) {
      return;
    }

    const iid = setInterval(() => savedCallback.current(), delay);
    return () => clearInterval(iid);
  }, [delay]);
}
