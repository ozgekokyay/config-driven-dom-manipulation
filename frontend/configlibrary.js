export default class ConfigLibrary {
    constructor(baseUrl = 'http://localhost:8080/api') {
        this.baseUrl = baseUrl;
    }

    // ----------- Configuration Endpoints --------------
    async getConfig(id) {
        const response = await fetch(`${this.baseUrl}/configuration/${id}`);
        return this._handleResponse(response);
    }

    async getAllConfigs() {
        const response = await fetch(`${this.baseUrl}/configuration/all`);
        return this._handleResponse(response);
    }

    async createConfig(config) {
        const response = await fetch(`${this.baseUrl}/configuration`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        return this._handleResponse(response);
    }

    async updateConfig(id, config) {
        const response = await fetch(`${this.baseUrl}/configuration/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        return this._handleResponse(response);
    }

    async deleteConfig(id) {
        const response = await fetch(`${this.baseUrl}/configuration/${id}`, {
            method: 'DELETE'
        });
        return this._handleResponse(response);
    }

    // ----------- SpecificConfig Endpoints --------------
    async getSpecificConfigByContext({ host, url, page }) {
        const params = new URLSearchParams();
        if (host) params.append('host', host);
        if (url) params.append('url', url);
        if (page) params.append('page', page);
        
        const response = await fetch(`${this.baseUrl}/specific?${params}`);
        return this._handleResponse(response);
    }

    async getSpecificConfigById(id) {
        const response = await fetch(`${this.baseUrl}/specific/${id}`);
        return this._handleResponse(response);
    }

    async getAllSpecificConfigs() {
        const response = await fetch(`${this.baseUrl}/specific/all`);
        return this._handleResponse(response);
    }

    async createSpecificConfig(config) {
        const response = await fetch(`${this.baseUrl}/specific`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        return this._handleResponse(response);
    }

    async createSpecificConfigFromYaml(yamlString) {
        const response = await fetch(`${this.baseUrl}/specific`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-yaml' },
            body: yamlString
        });
        return this._handleResponse(response);
    }

    async getSpecificConfigYamlById(id) {
        const response = await fetch(`${this.baseUrl}/specific/yaml/${id}`);
        return response.text();
    }

    async updateSpecificConfig(id, config) {
        const response = await fetch(`${this.baseUrl}/specific/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        return this._handleResponse(response);
    }

    async deleteSpecificConfig(id) {
        const response = await fetch(`${this.baseUrl}/specific/${id}`, {
            method: 'DELETE'
        });
        return this._handleResponse(response);
    }

    // ----------- Helper Methods --------------
    async _handleResponse(response) {
        try {
            if (!response.ok) {
                let errorText = await response.text();
                try {
                    const errorJson = JSON.parse(errorText);
                    if (errorJson.message) {
                        errorText = errorJson.message;
                    }
                } catch (e) {
                    // Not JSON, use as plain text
                }
                throw new Error(`Request failed: ${response.status} - ${errorText}`);
            }

            const contentType = response.headers.get('Content-Type') || '';
            
            if (contentType.includes('application/json')) {
                return await response.json();
            }
            
            if (contentType.includes('application/x-yaml') || contentType.includes('text/yaml')) {
                return await response.text();
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error handling response:', error);
            throw error;
        }
    }

    // ----------- DOM Manipulation --------------
    async applyConfig(config) {
        if (typeof config === 'string') {
            config = await this.getSpecificConfigById(config);
        } else if (config.host || config.url || config.page) {
            config = await this.getSpecificConfigByContext(config);
        }

        if (!config || !config.actions || !Array.isArray(config.actions)) {
            console.warn('Invalid configuration format');
            return;
        }

        // Execute actions in sequence
        for (const action of config.actions) {
            try {
                await this._executeAction(action);
            } catch (error) {
                console.error(`Error executing action:`, action, error);
            }
        }
    }

    _executeAction(action) {
        switch (action.type) {
            case 'remove':
                return this._removeElements(action.selector);
            case 'replace':
                return this._replaceElements(action.selector, action.newElement);
            case 'insert':
                return this._insertElement(action.position, action.target, action.element);
            case 'alter':
                return this._alterText(action.oldValue, action.newValue);
            default:
                console.warn(`Unknown action type: ${action.type}`);
        }
    }

    _removeElements(selector) {
        document.querySelectorAll(selector).forEach(el => el.remove());
    }

    _replaceElements(selector, newElementHtml) {
        document.querySelectorAll(selector).forEach(el => {
            const parent = el.parentNode;
            const temp = document.createElement('div');
            temp.innerHTML = newElementHtml;
            const newElement = temp.firstChild;
            if (parent && newElement) {
                parent.replaceChild(newElement, el);
            }
        });
    }

    _insertElement(position, targetSelector, elementHtml) {
        const targets = document.querySelectorAll(targetSelector);
        if (!targets.length) return;

        const temp = document.createElement('div');
        temp.innerHTML = elementHtml;
        const newElement = temp.firstChild;
        if (!newElement) return;

        targets.forEach(target => {
            const clone = newElement.cloneNode(true);
            switch (position.toLowerCase()) {
                case 'beforebegin':
                    target.before(clone);
                    break;
                case 'afterbegin':
                    target.prepend(clone);
                    break;
                case 'beforeend':
                    target.append(clone);
                    break;
                case 'afterend':
                    target.after(clone);
                    break;
                default:
                    console.warn(`Unknown position: ${position}`);
            }
        });
    }

    _alterText(oldValue, newValue) {
        const walker = document.createTreeWalker(
            document.body,
            NodeFilter.SHOW_TEXT,
            null,
            false
        );

        let node;
        while (node = walker.nextNode()) {
            node.textContent = node.textContent.replace(
                new RegExp(this._escapeRegExp(oldValue), 'g'),
                newValue
            );
        }
    }

    _escapeRegExp(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }
}

if (typeof window !== 'undefined') {
    window.ConfigLibrary = ConfigLibrary;
}