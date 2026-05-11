package com.emulator.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cpu")
@CrossOrigin(origins = "*") 
public class CpuController {

    @PostMapping("/execute")
    public Map<String, Object> executeInstructions(@RequestBody Map<String, Object> payload) {
        String instructions = (String) payload.getOrDefault("instructions", "");
        boolean stepMode = (Boolean) payload.getOrDefault("stepMode", false);

        System.out.println("Received Assembly: \n" + instructions);
        
        Map<String, Integer> regs = new HashMap<>();
        regs.put("A", 0); regs.put("B", 0); regs.put("C", 0); regs.put("D", 0);
        int pc = 0;

        Map<String, String> memory = new HashMap<>();

        String[] lines = instructions.split("\\r?\\n");
        while (pc < lines.length) {
            String line = lines[pc].trim().toUpperCase();
            if (line.isEmpty() || line.startsWith(";")) { pc++; continue; }
            
            memory.put(String.format("0x%04X", pc), line.split(" ")[0]);
            
            if (line.equals("HLT")) { pc++; break; }
            
            if (line.startsWith("MOV") || line.startsWith("ADD")) {
                String[] args = line.substring(3).split(",");
                if (args.length == 2) {
                    String dest = args[0].trim();
                    String src = args[1].trim();
                    
                    int val = regs.containsKey(src) ? regs.get(src) : Integer.parseInt(src);
                    
                    if (line.startsWith("MOV")) {
                        regs.put(dest, val & 0xFF); // & 0xFF keeps it 8-bit
                    } else if (line.startsWith("ADD")) {
                        regs.put(dest, (regs.get(dest) + val) & 0xFF);
                    }
                }
            }
            pc++;
        }

        Map<String, Object> response = new HashMap<>();
        Map<String, String> outRegs = new HashMap<>();
        for (String reg : regs.keySet()) { outRegs.put(reg, String.format("0x%02X", regs.get(reg))); }
        outRegs.put("PC", String.format("0x%04X", pc));
        outRegs.put("SP", "0xFFFF");
        
        response.put("registers", outRegs);
        response.put("flags", "Z=0 S=0 C=0 O=0");
        response.put("memory", memory);

        return response;
    }
}