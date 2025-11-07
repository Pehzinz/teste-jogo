package com.felipemelantonio.motorunneriot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.felipemelantonio.motorunneriot.MotoRunnerGame;

public class MenuScreen implements Screen {

    private final MotoRunnerGame game;

    // Tela que será renderizada "atrás" do menu (ex.: GameScreen rodando)
    private final Screen behindScreen;

    private SpriteBatch batch;
    private BitmapFont fontTitle, fontOptions;

    // Removemos o background sólido; manteremos logo se quiser
    private Texture logo;       // logo.png (opcional)
    private Sound hoverSound;   // hover.wav (opcional)
    private Sound clickSound;   // click.wav (opcional)
    private Music music;        // menu_music.mp3 (opcional)

    // Pixel 1x1 para desenhar retângulos com alpha
    private Texture pixel;

    private int selectedOption = 0;
    private String[] options = {
            "Fase 1 — Coordenação Inicial (2 faixas)",
            "Fase 2 — Resistência e Ritmo (3 faixas)",
            "Fase 3 — Potência e Reflexo (4 faixas)",
            "Sair"
    };

    private final GlyphLayout layout = new GlyphLayout();

    /**
     * @param game           seu Game
     * @param behindScreen   a tela a ser desenhada por trás (ex.: GameScreen em execução)
     */
    
    public MenuScreen(MotoRunnerGame game) {
        this(game, null);
    }
public MenuScreen(MotoRunnerGame game, Screen behindScreen) {
        this.game = game;
        this.behindScreen = behindScreen;
    }

    @Override
    public void show() {
        try { Gdx.input.setCatchKey(Input.Keys.BACK, true); } catch (Throwable t) {}
        optionBounds = null;
        Gdx.app.log("Menu", "show()");
        batch = new SpriteBatch();
        fontTitle = new BitmapFont();
        fontOptions = new BitmapFont();
        fontTitle.setColor(Color.GOLD);
        fontOptions.setColor(Color.WHITE);

        // Cria um pixel branco 1x1 para "pintar" retângulos com batch.setColor(...)
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        logo       = tryLoadTexture("logo.png");
        hoverSound = tryLoadSound("hover.wav");
        clickSound = tryLoadSound("click.wav");
        music      = tryLoadMusic("menu_music.mp3");

        if (music != null) {
            music.setLooping(true);
            music.setVolume(0.4f);
            music.play();
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        // NÃO limpar a tela com preto — queremos ver o que está atrás
        // Desenhar a tela de jogo por trás (se fornecida)
        if (behindScreen != null) {
            // Importante: deixar a tela de trás fazer o clear dela (normal do GameScreen),
            // e logo depois desenhamos o overlay transparente por cima
            behindScreen.render(delta);
        } else {
            // fallback (se não houver uma tela por trás, pelo menos não fica lixo)
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        final int W = Gdx.graphics.getWidth();
        final int H = Gdx.graphics.getHeight();

        batch.begin();

        // Scrim sutil no fundo: um véu translúcido pra legibilidade (ex.: preto 35%)
        drawRect(0, 0, W, H, new Color(0, 0, 0, 0.35f));

        // Título
        fontTitle.getData().setScale(2.2f);
        String title = "🏍️  MOTO RUNNER IoT";
        layout.setText(fontTitle, title);
        float titleX = (W - layout.width) / 2f;
        float titleY = H * 0.78f;
        // Fundo do título (leve) para destacar
        float titlePadX = 18f, titlePadY = 10f;
        drawRect(titleX - titlePadX, titleY - layout.height - titlePadY,
                 layout.width + 2 * titlePadX, layout.height + 2 * titlePadY,
                 new Color(0f, 0f, 0f, 0.25f));

        fontTitle.draw(batch, title, titleX, titleY);

        // Logo (opcional)
        if (logo != null) {
            float lx = (W - logo.getWidth()) / 2f;
            float ly = titleY - 140f;
            batch.draw(logo, lx, ly);
        }

        // Opções — retângulos com alpha
        fontOptions.getData().setScale(1.3f);
        float startY = H * 0.48f;
        float x = Math.max(140f, W * 0.15f);
        float lineH = 58f; // altura da célula/retângulo

        for (int i = 0; i < options.length; i++) {
            boolean sel = (i == selectedOption);
            String text = options[i];

            layout.setText(fontOptions, text);
            float boxW = Math.max(layout.width + 40f, W * 0.7f); // largura mínima
            float boxH = lineH;
            float y = startY - i * (boxH + 12f);

            // Fundo do item (transparente). Se selecionado, um pouco mais opaco
            Color boxColor = sel ? new Color(0f, 0f, 0f, 0.55f) : new Color(0f, 0f, 0f, 0.35f);
            drawRect(x - 20f, y - boxH + 8f, boxW, boxH, boxColor);

            // Borda sutil se selecionado
            if (sel) {
                drawRect(x - 20f, y - boxH + 8f, boxW, 2f, new Color(1f, 1f, 0f, 0.85f)); // top
                drawRect(x - 20f, y + 8f, boxW, 2f, new Color(1f, 1f, 0f, 0.85f)); // bottom
                drawRect(x - 20f, y - boxH + 8f, 2f, boxH, new Color(1f, 1f, 0f, 0.85f)); // left
                drawRect(x - 20f + boxW - 2f, y - boxH + 8f, 2f, boxH, new Color(1f, 1f, 0f, 0.85f)); // right
            }

            fontOptions.setColor(sel ? Color.YELLOW : Color.WHITE);
            float textX = x;
            float textY = y;
            fontOptions.draw(batch, text, textX, textY);
        }

        // Hint
        fontOptions.setColor(Color.LIGHT_GRAY);
        fontOptions.getData().setScale(1f);
        String hint = "↑/↓ ou 1/2/3 | ENTER/ESPAÇO para selecionar | ESC para sair";
        layout.setText(fontOptions, hint);
        float hintX = (W - layout.width) / 2f;
        float hintY = Math.max(60f, H * 0.08f);
        drawRect(hintX - 12f, hintY - layout.height - 8f,
                 layout.width + 24f, layout.height + 16f, new Color(0f, 0f, 0f, 0.25f));
        fontOptions.draw(batch, hint, hintX, hintY);

        
// Build clickable bounds for options and handle hover/click
if (optionBounds == null || optionBounds.length != options.length) {
    optionBounds = new Rectangle[options.length];
}
// We need to recompute layout-based rectangles with same geometry used when drawing
for (int i = 0; i < options.length; i++) {
    String text = options[i];
    layout.setText(fontOptions, text);
    float y = startY - i * spacingY;
    float boxW = Math.max(layout.width + 40f, 320f);
    float boxH = Math.max(layout.height + 24f, 48f);
    float x = (W - boxW + 40f) / 2f; // align with the drawRect x - 20f
    optionBounds[i] = new Rectangle(x - 20f, y - boxH + 8f, boxW, boxH);
}
float mouseX = Gdx.input.getX();
float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
for (int i = 0; i < options.length; i++) {
    if (optionBounds[i] != null && optionBounds[i].contains(mouseX, mouseY)) {
        if (selectedOption != i && hoverSound != null) hoverSound.play(0.25f);
        selectedOption = i;
        break;
    }
}
if (Gdx.input.justTouched() || Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
    confirmSelection();
}
batch.end();

    }

    private void drawRect(float x, float y, float w, float h, Color color) {
        // Usa o pixel 1x1 esticado, com cor/alpha desejados
        batch.setColor(color);
        batch.draw(pixel, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    private void handleInput() {
        // Navegação
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) changeSelection(-1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) changeSelection(1);

        // Atalhos por número
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { selectedOption = 0; confirmSelection(); }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { selectedOption = 1; confirmSelection(); }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { selectedOption = 2; confirmSelection(); }

        // Confirmar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            confirmSelection();
        }

        // Sair
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopAudio();
            Gdx.app.exit();
        }
    }

    private void changeSelection(int dir) {
        int prev = selectedOption;
        selectedOption = (selectedOption + dir + options.length) % options.length;
        if (hoverSound != null && selectedOption != prev) hoverSound.play(0.4f);
        Gdx.app.log("Menu", "selectedOption=" + selectedOption);
    }

    private void confirmSelection() {
        if (clickSound != null) clickSound.play();
        Gdx.app.log("Menu", "confirm -> option " + selectedOption);
        stopAudio(); // para a música antes de trocar de tela

        switch (selectedOption) {
            case 0:
                game.setScreen(new GameScreen(game, 1));
                break;
            case 1:
                game.setScreen(new GameScreen(game, 2));
                break;
            case 2:
                game.setScreen(new GameScreen(game, 3));
                break;
            case 3:
                Gdx.app.exit();
                break;
            default:
                break;
        }
        // NÃO chamar dispose() aqui
    }

    private void stopAudio() {
        if (music != null) music.stop();
    }

    private Texture tryLoadTexture(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) return null;
            Texture t = new Texture(path);
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return t;
        } catch (Exception e) {
            Gdx.app.error("Menu", "Erro ao carregar textura: " + path, e);
            return null;
        }
    }

    private Sound tryLoadSound(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) return null;
            return Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("Menu", "Erro ao carregar som: " + path, e);
            return null;
        }
    }

    private Music tryLoadMusic(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) return null;
            return Gdx.audio.newMusic(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("Menu", "Erro ao carregar música: " + path, e);
            return null;
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (fontTitle != null) fontTitle.dispose();
        if (fontOptions != null) fontOptions.dispose();
        if (logo != null) logo.dispose();
        if (hoverSound != null) hoverSound.dispose();
        if (clickSound != null) clickSound.dispose();
        if (music != null) music.dispose();
        if (pixel != null) pixel.dispose();
        // NÃO dispose do behindScreen aqui — ele é gerenciado fora
    }
}
