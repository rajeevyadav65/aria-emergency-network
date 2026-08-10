package com.emergency.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewController {
    @GetMapping(value = "/dashboard", produces = "text/html")
    public String showDashboard() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Smart Sahayata - Smart Dispatch & Discovery</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <style>
                    body { margin: 0; display: flex; flex-direction: column; height: 100vh; font-family: sans-serif; overflow: hidden; }
                    #map { flex-grow: 1; width: 100%; z-index: 1;}
                    .status-bar { background: #0D47A1; color: white; padding: 15px; font-weight: bold; display: flex; justify-content: space-between; z-index: 1001; }
                    #readyAlert { position: absolute; bottom: 20px; left: 20px; background: #FFD600; padding: 10px; border-radius: 5px; display: none; z-index: 9999; font-weight: bold; border: 2px solid #000; }
                </style>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
            </head>
            <body>
                <div class="status-bar">
                    <span>🚑 SMART SAHAYATA: DISPATCH HUB</span>
                    <span id="conn-status">Connecting...</span>
                </div>
                <div id="readyAlert">⚠️ RESPONDER STANDBY: Disaster in progress!</div>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <script>
                    var map = L.map('map').setView([27.4924, 77.6737], 14);
                    L.tileLayer('https://{s}.google.com/vt/lyrs=m,traffic&x={x}&y={y}&z={z}', {maxZoom: 20, subdomains:['mt0','mt1','mt2','mt3']}).addTo(map);

                    function getRiskColor(level) {
                        if (level === 'HIGH') return 'red';
                        if (level === 'MEDIUM') return 'yellow';
                        if (level === 'LOW') return 'green';
                        return 'blue';
                    }

                    var socket = new SockJS('/ws');
                    var stompClient = Stomp.over(socket);
                    stompClient.connect({}, function (frame) {
                        document.getElementById('conn-status').innerText = "LIVE ✅";

                        stompClient.subscribe('/topic/emergencies', function (msg) {
                            var e = JSON.parse(msg.body);
                            var color = getRiskColor(e.riskLevel);
                            
                            // 🟢 Draw Color-Coded Risk Zone
                            L.circle([e.latitude, e.longitude], {
                                color: color, fillColor: color, fillOpacity: 0.3, radius: e.impactRadius || 300
                            }).addTo(map).bindPopup("<b>Risk: " + e.riskLevel + "</b><br>" + e.message).openPopup();
                            
                            map.flyTo([e.latitude, e.longitude], 15);
                        });

                        stompClient.subscribe('/topic/responders/readiness', function (msg) {
                            var alertDiv = document.getElementById('readyAlert');
                            alertDiv.innerText = msg.body;
                            alertDiv.style.display = "block";
                            setTimeout(() => { alertDiv.style.display = "none"; }, 10000);
                        });
                    });
                </script>
            </body>
            </html>
            """;
    }
}