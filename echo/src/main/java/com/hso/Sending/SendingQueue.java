package com.hso.Sending;

/**
 * @author Aaron Moser
 */

import java.util.concurrent.LinkedBlockingQueue;

import com.hso.Messages.Sendable;
 
public class SendingQueue extends LinkedBlockingQueue<Sendable> {}