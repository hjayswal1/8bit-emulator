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

        // Initialize Status Flags
        int flagZ = 0; // Zero Flag
        int flagS = 0; // Sign Flag
        int flagC = 0; // Carry Flag
        int flagO = 0; // Overflow Flag

        Map<String, String> memory = new HashMap<>();

        String[] lines = instructions.split("\\r?\\n");
        while (pc < lines.length) {
            String line = lines[pc].trim().toUpperCase();
            if (line.isEmpty() || line.startsWith(";")) { pc++; continue; }
            
            memory.put(String.format("0x%04X", pc), line.split(" ")[0]);
            
            if (line.equals("HLT")) { pc++; break; }
            
            if (line.startsWith("MOV") || line.startsWith("ADD") || line.startsWith("SUB")) {
                String[] args = line.substring(3).split(",");
                if (args.length == 2) {
                    String dest = args[0].trim();
                    String src = args[1].trim();
                    
                    int val;
                    if (regs.containsKey(src)) {
                        val = regs.get(src);
                    } else if (src.startsWith("0X")) {
                        val = Integer.parseInt(src.substring(2), 16);
                    } else {
                        val = Integer.parseInt(src);
                    }
                    
                    if (line.startsWith("MOV")) {
                        int result = val & 0xFF;
                        regs.put(dest, result);
                        
                        // MOV typically updates the Zero and Sign flags, but not Carry or Overflow
                        flagZ = (result == 0) ? 1 : 0;
                        flagS = ((result & 0x80) != 0) ? 1 : 0;
                    } else if (line.startsWith("ADD")) {
                        int regVal = regs.get(dest);
                        int result = regVal + val;
                        int result8Bit = result & 0xFF; // Constrain to 8 bits
                        
                        regs.put(dest, result8Bit);
                        
                        // Calculate all flags for ADD
                        flagC = (result > 255) ? 1 : 0;
                        flagZ = (result8Bit == 0) ? 1 : 0;
                        flagS = ((result8Bit & 0x80) != 0) ? 1 : 0;
                        flagO = (((regVal ^ result8Bit) & (val ^ result8Bit) & 0x80) != 0) ? 1 : 0;
                    } else if (line.startsWith("SUB")) {
                        int regVal = regs.get(dest);
                        int result = regVal - val;
                        int result8Bit = result & 0xFF; // Constrain to 8 bits
                        
                        regs.put(dest, result8Bit);
                        
                        // Calculate all flags for SUB
                        flagC = (regVal < val) ? 1 : 0; // Carry acts as a Borrow flag in subtraction
                        flagZ = (result8Bit == 0) ? 1 : 0;
                        flagS = ((result8Bit & 0x80) != 0) ? 1 : 0;
                        flagO = (((regVal ^ val) & (regVal ^ result8Bit) & 0x80) != 0) ? 1 : 0;
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
        
        String flagsStr = String.format("Z=%d S=%d C=%d O=%d", flagZ, flagS, flagC, flagO);
        response.put("flags", flagsStr);
        response.put("memory", memory);

        return response;
    }
}