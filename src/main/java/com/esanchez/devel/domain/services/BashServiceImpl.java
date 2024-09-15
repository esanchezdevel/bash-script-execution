package com.esanchez.devel.domain.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class BashServiceImpl implements BashService {

	@Override
	public SseEmitter executeCommand(String argument1, String argument2) {
	       SseEmitter emitter = new SseEmitter();
	        ExecutorService executor = Executors.newSingleThreadExecutor();

	        executor.execute(() -> {
	            Path tempScriptPath = null;
	            try {
	                // Obtener el script desde src/main/resources/scripts/
	                InputStream scriptStream = getClass().getClassLoader().getResourceAsStream("script/my-script.sh");
	                if (scriptStream == null) {
	                    emitter.send("Script not found!");
	                    emitter.complete();
	                    return;
	                }

	                // Crear un archivo temporal para ejecutar el script
	                tempScriptPath = Files.createTempFile("my-script", ".sh");
	                Files.copy(scriptStream, tempScriptPath, StandardCopyOption.REPLACE_EXISTING);
	                scriptStream.close();

	                // Hacer el script ejecutable
	                tempScriptPath.toFile().setExecutable(true);

	                // Ejecutar el script desde el archivo temporal
	                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", tempScriptPath.toString(), argument1, argument2);
	                Process process = processBuilder.start();

	                // Leer la salida estándar del script
	                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	                    String line;
	                    while ((line = reader.readLine()) != null) {
	                        emitter.send(line); // Enviar cada línea al cliente
	                    }
	                }

	                // Leer la salida de error del script
	                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
	                    String errorLine;
	                    while ((errorLine = errorReader.readLine()) != null) {
	                        emitter.send("ERROR: " + errorLine); // Enviar los errores al cliente
	                    }
	                }

	                // Esperar a que termine el proceso
	                int exitCode = process.waitFor();
	                emitter.send("Script finished with exit code: " + exitCode);
	                emitter.complete();

	            } catch (IOException | InterruptedException e) {
	                try {
	                    emitter.send("Error: " + e.getMessage());
	                } catch (IOException ex) {
	                    ex.printStackTrace();
	                }
	                emitter.completeWithError(e);
	            } finally {
	                // Limpiar el archivo temporal
	                if (tempScriptPath != null) {
	                    try {
	                        Files.deleteIfExists(tempScriptPath);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        });

	        return emitter;
	}
}
