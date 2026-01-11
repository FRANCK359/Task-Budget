# relancer.ps1
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RELANCE COMPLÈTE DE L'APPLICATION DOCKER" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Étape 1: Nettoyage
Write-Host "🧹 Étape 1: Nettoyage Docker..." -ForegroundColor Yellow
docker-compose down 2>$null
docker system prune -a -f --volumes 2>$null
Write-Host "✓ Nettoyage terminé" -ForegroundColor Green

# Étape 2: Build Maven
Write-Host "`n🔨 Étape 2: Build Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors du build Maven" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build Maven terminé" -ForegroundColor Green

# Étape 3: Build Docker
Write-Host "`n🐳 Étape 3: Build Docker..." -ForegroundColor Yellow
docker-compose build --no-cache
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors du build Docker" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build Docker terminé" -ForegroundColor Green

# Étape 4: Lancement
Write-Host "`n🚀 Étape 4: Lancement des services..." -ForegroundColor Yellow
docker-compose up -d
Write-Host "✓ Services lancés" -ForegroundColor Green

# Étape 5: Attente et vérification
Write-Host "`n⏳ Attente du démarrage (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Étape 6: Vérification
Write-Host "`n🔍 Étape 5: Vérification..." -ForegroundColor Cyan
Write-Host ""

# Vérifier PostgreSQL
Write-Host "📊 PostgreSQL:" -ForegroundColor White
$postgresStatus = docker-compose ps postgres | Select-String "Up"
if ($postgresStatus) {
    Write-Host "  ✓ PostgreSQL est en cours d'exécution" -ForegroundColor Green
} else {
    Write-Host "  ✗ PostgreSQL ne fonctionne pas" -ForegroundColor Red
}

# Vérifier l'application
Write-Host "`n📱 Application Spring Boot:" -ForegroundColor White
$appStatus = docker-compose ps app | Select-String "Up"
if ($appStatus) {
    Write-Host "  ✓ L'application est en cours d'exécution" -ForegroundColor Green

    # Tester l'API
    Write-Host "`n🌐 Test de l'API..." -ForegroundColor White
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/public/test" -TimeoutSec 10
        Write-Host "  ✓ API accessible: $($response.Content)" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠ API non accessible (peut être en cours de démarrage)" -ForegroundColor Yellow
    }
} else {
    Write-Host "  ✗ L'application ne fonctionne pas" -ForegroundColor Red
}

# Afficher les logs
Write-Host "`n📋 Commandes utiles:" -ForegroundColor Cyan
Write-Host "  Voir les logs: docker-compose logs -f app" -ForegroundColor White
Write-Host "  Arrêter: docker-compose down" -ForegroundColor White
Write-Host "  Redémarrer: docker-compose restart app" -ForegroundColor White
Write-Host "  Shell dans l'app: docker-compose exec app sh" -ForegroundColor White

Write-Host "`n✅ Relance terminée!" -ForegroundColor Green