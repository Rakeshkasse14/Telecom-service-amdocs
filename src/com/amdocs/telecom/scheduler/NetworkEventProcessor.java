package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.dto.TicketCreateDTO;
import com.amdocs.telecom.enums.IncidentCategory;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.model.NetworkEvent;
import com.amdocs.telecom.service.TicketService;
import com.amdocs.telecom.service.impl.TicketServiceImpl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Demonstrates Multithreading, BlockingQueue, Producer-Consumer Pattern, and Synchronization.
 * Consumes network alarm events and auto-raises tickets for critical faults.
 */
public class NetworkEventProcessor implements Runnable {

    private static final BlockingQueue<NetworkEvent> eventQueue = new LinkedBlockingQueue<>();
    private final TicketService ticketService = new TicketServiceImpl();
    private volatile boolean running = true;

    public static void publishEvent(NetworkEvent event) {
        eventQueue.offer(event);
        System.out.println("[NETWORK EVENT GENERATED] " + event);
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        System.out.println("[THREAD STARTED] NetworkEventProcessor worker thread is running...");
        while (running) {
            try {
                // Blocking call waiting for events (Consumer)
                NetworkEvent event = eventQueue.take();
                processEvent(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[EVENT ERROR] Error processing network event: " + e.getMessage());
            }
        }
        System.out.println("[THREAD STOPPED] NetworkEventProcessor shutting down.");
    }

    private void processEvent(NetworkEvent event) {
        System.out.println("[PROCESSING EVENT] Node: " + event.getNetworkNode() + " | Type: " + event.getEventType() + " | Severity: " + event.getSeverity());

        if ("CRITICAL".equalsIgnoreCase(event.getSeverity()) || "LINK_DOWN".equalsIgnoreCase(event.getEventType())) {
            // Auto create trouble ticket for critical network event
            TicketCreateDTO dto = new TicketCreateDTO(
                    101, // Customer ID (Default enterprise node owner)
                    501, // Service ID
                    IncidentCategory.NETWORK_OUTAGE,
                    "Auto-generated ticket from Network Fault Event: " + event.getEventType() + " on Node " + event.getNetworkNode(),
                    TicketPriority.CRITICAL,
                    "CRITICAL"
            );
            ticketService.createTicket(dto);
            System.out.println(">> AUTO-CREATED CRITICAL TROUBLE TICKET for Node: " + event.getNetworkNode());
        }
    }
}
