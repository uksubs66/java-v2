
package com.foretees.common;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class offers a timeout feature on socket connections. A maximum length
 * of time allowed for a connection can be specified, along with a host and
 * port.
 *
 * @author David Reilly
 */
public final class TimedSocket {

        /**
         * This class used as utility class.
         */
        private TimedSocket() {
        }

        /**
         * Attempts to connect to a service at the specified address and port, for a
         * specified maximum amount of time.
         *
         * @param addr
         *            Address of host
         * @param port
         *            Port of service
         * @param delay
         *            Delay in milliseconds
         * @return Socket
         * @throws InterruptedIOException
         *             operation interrupted exception
         * @throws IOException
         *             Input/Output exception
         */
        public static Socket getSocket(final InetAddress addr, final int port,
                        final int delay) throws InterruptedIOException, IOException {
                // Create a new socket thread, and start it running
                SocketThread st = new SocketThread(addr, port);
                st.start();

                int timer = 0;
                Socket sock = null;

                for (;;) {
                        // Check to see if a connection is established
                        if (st.isConnected()) {
                                // Yes ... assign to sock variable, and break out of loop
                                sock = st.getSocket();
                                break;
                        } else {
                                // Check to see if an error occurred
                                if (st.isError()) {
                                        // No connection could be established
                                        throw (st.getException());
                                }

                                try {
                                        // Sleep for a short period of time
                                        Thread.sleep(POLL_DELAY);
                                } catch (InterruptedException ie) {
                                        // omit it
                                }

                                // Increment timer
                                timer += POLL_DELAY;

                                // Check to see if time limit exceeded
                                if (timer > delay) {
                                        // Can't connect to server
                                        throw new InterruptedIOException("Could not connect for "
                                                        + delay + " milliseconds");
                                }
                        }
                }

                return sock;
        }

        /**
         * Attempts to connect to a service at the specified address and port, for a
         * specified maximum amount of time.
         *
         * @param host
         *            Hostname of machine
         * @param port
         *            Port of service
         * @param delay
         *            Delay in milliseconds
         * @return Socket
         * @throws InterruptedIOException
         *             operation interrupted exception
         * @throws IOException
         *             Input/Output exception
         */
        public static Socket getSocket(final String host, final int port,
                        final int delay) throws InterruptedIOException, IOException {
                // Convert host into an InetAddress, and call getSocket method
                InetAddress inetAddr = InetAddress.getByName(host);

                return getSocket(inetAddr, port, delay);
        }

        /**
         * Inner class for establishing a socket thread within another thread, to
         * prevent blocking.
         *
         * @author David Reilly
         *
         */
        static class SocketThread extends Thread {
                /**
                 * Socket connection to remote host.
                 */
                private volatile Socket mconnection = null;
                /**
                 * Hostname to connect to.
                 */
                private String mhost = null;
                /**
                 * Internet Address to connect to.
                 */
                private InetAddress minet = null;
                /**
                 * Port number to connect to.
                 */
                private int mport = 0;
                /**
                 * Exception in the event a connection error occurs.
                 */
                private IOException mexception = null;

                /**
                 * Connect to the specified host and port number.
                 *
                 * @param host  host connecting to
                 * @param port  port connecting to
                 */
                public SocketThread(final String host, final int port) {
                        // Assign to member variables
                        mhost = host;
                        mport = port;
                }

                /**
                 * Connect to the specified host IP and port number.
                 *
                 * @param inetAddr      addr connecting to
                 * @param port          port connecting to
                 */
                public SocketThread(final InetAddress inetAddr, final int port) {
                        // Assign to member variables
                        minet = inetAddr;
                        mport = port;
                }

                /**
                 * Execution flow.
                 */
                public void run() {
                        // Socket used for establishing a connection
                        Socket sock;

                        try {
                                // Was a string or an inet specified
                                if (mhost != null) {
                                        // Connect to a remote host - BLOCKING I/O
                                        sock = new Socket(mhost, mport);
                                } else {
                                        // Connect to a remote host - BLOCKING I/O
                                        sock = new Socket(minet, mport);
                                }
                        } catch (IOException ioe) {
                                // Assign to our exception member variable
                                mexception = ioe;
                                return;
                        }

                        // If socket constructor returned without error,
                        // then connection finished
                        mconnection = sock;
                }

                /**
                 * Are we connected?
                 *
                 * @return      connected status
                 */
                public boolean isConnected() {
            return mconnection != null;
                }

                /**
                 * Did an error occur?
                 *
                 * @return      error occur status
                 */
                public boolean isError() {
            return mexception != null;
                }

                /**
                 * Get socket.
                 *
                 * @return      socket
                 */
                public Socket getSocket() {
                        return mconnection;
                }

                /**
                 * Get exception.
                 *
                 * @return      exception
                 */
                public IOException getException() {
                        return mexception;
                }
        }

        /**
         * Polling delay for socket checks (in milliseconds).
         */
        private static final int POLL_DELAY = 100;
}

